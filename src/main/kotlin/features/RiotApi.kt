package features

import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Queue
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.summoner.Summoner
import data.LeaguePlayer
import data.PlayerProvider
import data.SecretProvider
import data.Settings
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.Time
import java.text.DecimalFormat

@ExperimentalStdlibApi
enum class RiotApi {
    INSTANCE;

    private val GAME_OVER_MESSAGE_TEMPLATE = "%s%s  %s hat %s\nScore: %s"
    private val GAME_START_MESSAGE_TEMPLATE = ":video_game:%s  %s is ingame!\nPlaying %s in %s\n%s"
    private val GAME_RANKED_QUEUE_MESSAGE_TEMPLATE = "%s  %s  %s   %s%%  LP:  %d%s"
    private val GAME_RANKED_QUEUE_PROMO_MESSAGE_TEMPLATE = "   %s"

    private val SCORE_TEMPLATE = "[ K / D / A | M ( MPM )]"

    private val WON_EMOTE = ":white_check_mark:"
    private val LOSS_EMOTE = ":no_entry_sign:"

    private val summonerList = arrayListOf<Summoner>()
    private lateinit var bot: Kord

    private var initialized = false

    private val playerInfoMap: MutableMap<String, LeaguePlayer> = hashMapOf()

    fun initialize() {
        if (initialized) return

        this.bot = DiscordBot.INSTANCE.bot

        try {
            Orianna.setRiotAPIKey(SecretProvider.INSTANCE.get("riot-api").secret)
            Orianna.setDefaultLocale("en_US")
            Orianna.setDefaultRegion(Region.EUROPE_WEST)

            PlayerProvider.INSTANCE.players.forEach { (name, player) ->
                summonerList.add(Orianna.summonerNamed(name).get())
                playerInfoMap[name] = player
            }
            start()
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
        }
    }

    private fun start() {
        GlobalScope.launch {
            while (true) {
//                bot.editPresence {
//                    playing("Scanning")
//                }
                summonerList.forEach { player ->
                    evalPlayerStatus(player)
                }
//                bot.editPresence {
//                    playing("Waiting")
//                }
                delay(60000L)
            }
        }.invokeOnCompletion {
            runBlocking {
                bot.editPresence {
                    playing("ERROR RIOT")
                }
            }
            start()
        }
    }

    private suspend fun evalPlayerStatus(summoner: Summoner) {
        val player = playerInfoMap[summoner.name] ?: throw NoSuchElementException("${summoner.name} no in playerInfoMap")
        //game over
        if (player.ingame && !summoner.isInGame) {
            player.ingame = false
            evalGameOver(summoner)
            return
        }
        //game started
        if (!player.ingame && summoner.isInGame) {
            player.ingame = true
            player.lastMatchId = summoner.currentMatch.id
            evalGameStart(summoner)
        }
    }

    private suspend fun evalGameStart(summoner: Summoner) {
        val player = playerInfoMap[summoner.name] ?: throw NoSuchElementException("${summoner.name} no in playerInfoMap")
        val match = summoner.currentMatch
        val queue = match.queue
        val champion = match.participants.filterNotNull().find { it.summoner.name == summoner.name }?.champion
        val league = summoner.getLeaguePosition(if (queue == Queue.RANKED_SOLO || queue == Queue.RANKED_FLEX) queue else Queue.RANKED_SOLO)

        val winrate = {
            val winrate = league.wins.toDouble() / (league.wins + league.losses).toDouble() * 100
            val df = DecimalFormat("#.##")
            df.format(winrate)
        }()

        val promo = league.promos

        val promoText = if (promo == null) "" else String.format(GAME_RANKED_QUEUE_PROMO_MESSAGE_TEMPLATE, promo.progess)

        val rankedInfo = if (queue == Queue.RANKED_SOLO || queue == Queue.RANKED_FLEX)
            String.format(GAME_RANKED_QUEUE_MESSAGE_TEMPLATE, (if (queue == Queue.RANKED_SOLO) "SoloQ" else "Flex"), league.tier, league.division, winrate, league.leaguePoints, promoText)
        else
            ""

        val gameInfo = String.format(GAME_START_MESSAGE_TEMPLATE, Time.getTime(), player.realName, champion?.name, match.queue.name, rankedInfo)
        sendDiscordMessage(gameInfo)
    }

    private suspend fun evalGameOver(summoner: Summoner) {
        val player = playerInfoMap[summoner.name] ?: throw NoSuchElementException("${summoner.name} no in playerInfoMap")
        val match = Orianna.matchWithId(player.lastMatchId).get()
        val participant = match.participants.filterNotNull().find { it.summoner.name == summoner.name }.takeIf { it != null }
        val won = participant?.team?.isWinner
        val stats = participant?.stats

        val score = {
            val retVal = SCORE_TEMPLATE
            val kills = "${stats!!.kills}"
            val deaths = "${stats.deaths}"
            val assists = "${stats.assists}"
            val minions = stats.creepScore + stats.neutralMinionsKilled;
            val minionsString = "${stats.creepScore + stats.neutralMinionsKilled}"
            val minionsPerMinute = "${(minions / match.duration.standardMinutes)}"

            retVal.replace("K", kills)
                .replace("D", deaths)
                .replace("A", assists)
                .replace("MPM", minionsPerMinute)
                .replace("M", minionsString)
        }()

        val message = String.format(GAME_OVER_MESSAGE_TEMPLATE, (if (won!!) WON_EMOTE else LOSS_EMOTE), Time.getTime(), player.realName, (if (won) "GEWONNEN" else "VERLOREN"), score)
        sendDiscordMessage(message)
    }

    private suspend fun sendDiscordMessage(message: String) {
        val settings = Settings.instance

        val server = bot.getGuild(Snowflake(settings.league_tracker_server_id)) ?: throw NoSuchElementException("serverId ${settings.league_tracker_server_id} does not exist")
        val channel = server.getChannel(Snowflake(settings.league_tracker_channel))

        if (channel !is TextChannel) return
        channel.createMessage(message)
    }
}
