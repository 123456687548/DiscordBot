package data.github.request.latestRelease

import java.util.*

class Asset {
    var url: String? = null
    var id = 0
    var node_id: String? = null
    var name: String? = null
    var label: Any? = null
    var uploader: Uploader? = null
    var content_type: String? = null
    var state: String? = null
    var size = 0
    var download_count = 0
    var created_at: Date? = null
    var updated_at: Date? = null
    var browser_download_url: String? = null
}
