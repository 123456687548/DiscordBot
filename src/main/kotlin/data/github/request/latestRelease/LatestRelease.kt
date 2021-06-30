package data.github.request.latestRelease

import java.util.*

class LatestRelease {
    var url: String? = null
    var assets_url: String? = null
    var upload_url: String? = null
    var html_url: String? = null
    var id = 0
    var author: Author? = null
    var node_id: String? = null
    var tag_name: String? = null
    var target_commitish: String? = null
    var name: String? = null
    var draft = false
    var prerelease = false
    var created_at: Date? = null
    var published_at: Date? = null
    var assets: List<Asset>? = null
    var tarball_url: String? = null
    var zipball_url: String? = null
    var body: String? = null
}