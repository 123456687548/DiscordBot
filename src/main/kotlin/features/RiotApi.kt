package features

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
import no.stelar7.api.r4j.basic.APICredentials
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType
import no.stelar7.api.r4j.impl.R4J
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner
import util.Time
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

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

    private lateinit var r4j: R4J

    fun initialize() {
        if (initialized) return

        this.bot = DiscordBot.INSTANCE.bot

        try {
            r4j = R4J(APICredentials(SecretProvider.INSTANCE.get("riot-api").secret))
//            Orianna.setRiotAPIKey(SecretProvider.INSTANCE.get("riot-api").secret)
//            Orianna.setDefaultLocale("en_US")
//            Orianna.setDefaultRegion(Region.EUROPE_WEST)
            PlayerProvider.INSTANCE.players.forEach { (name, player) ->
                summonerList.add(r4j.loLAPI.summonerAPI.getSummonerByName(LeagueShard.EUW1, name))
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
        }.invokeOnCompletion { throwable ->
            runBlocking {
                throwable?.let { it1 -> DiscordBot.INSTANCE.sendErrorMessage(it1) }

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
        if (player.ingame && summoner.currentGame != null) {
            player.ingame = false
            evalGameOver(summoner)
            return
        }
        //game started
        if (!player.ingame && summoner.currentGame != null) {
            player.ingame = true
            player.lastMatchId = "EUW1_${summoner.currentGame.gameId}"
            evalGameStart(summoner)
        }
    }

    private fun getWinrateString(leagueEntry: LeagueEntry?): String {
        if (leagueEntry == null) return ""

        val winrate = leagueEntry.wins.toDouble() / (leagueEntry.wins + leagueEntry.losses).toDouble() * 100
        val df = DecimalFormat("#.##")
        return df.format(winrate)
    }

    private suspend fun evalGameStart(summoner: Summoner) {
        val player = playerInfoMap[summoner.name] ?: throw NoSuchElementException("${summoner.name} no in playerInfoMap")
        val match = summoner.currentGame
        val queue = match.gameQueueConfig
        val champion = match.participants.filterNotNull().find { it.summonerName == summoner.name }?.championId
        val league = summoner.leagueEntry.first { it.queueType == queue }


        val winrate = getWinrateString(league)

        val promo = league.miniSeries

        val promoText = if (league.isInPromos) "" else String.format(GAME_RANKED_QUEUE_PROMO_MESSAGE_TEMPLATE, promo.progress)

        val rankedInfo = if (league != null && (queue == GameQueueType.RANKED_SOLO_5X5 || queue == GameQueueType.RANKED_FLEX_SR))
            String.format(GAME_RANKED_QUEUE_MESSAGE_TEMPLATE, (if (queue == GameQueueType.RANKED_SOLO_5X5) "SoloQ" else "Flex"), league.tier, league.tierDivisionType.prettyName(), winrate, league.leaguePoints, promoText)
        else
            ""
//todo get champ name
        val gameInfo = String.format(GAME_START_MESSAGE_TEMPLATE, Time.getTime(), player.realName, champion, queue.name, rankedInfo)
        sendDiscordMessage(gameInfo)
    }

    private suspend fun evalGameOver(summoner: Summoner) {
        val player = playerInfoMap[summoner.name] ?: throw NoSuchElementException("${summoner.name} no in playerInfoMap")

        val match = r4j.loLAPI.matchAPI.getMatch(RegionShard.EUROPE, player.lastMatchId)
        val participant = match.participants.filterNotNull().find { it.summonerName == summoner.name }.takeIf { it != null }
        val won = participant?.didWin()
        val stats = participant?.kills

        val score = {
            val retVal = SCORE_TEMPLATE
            val kills = "${participant!!.kills}"
            val deaths = "${participant.deaths}"
            val assists = "${participant.assists}"
            val minions = participant.totalMinionsKilled
            val minionsString = "${minions}"
            val startTime = match.gameStartAsDate
            val endTime = match.gameEndAsDate
            val gameTimeInMinutes = ChronoUnit.MINUTES.between(startTime, endTime)
            val minionsPerMinute = "${(minions / gameTimeInMinutes)}"

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
