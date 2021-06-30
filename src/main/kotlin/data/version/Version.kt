package data.version

class Version private constructor(val majorReleaseNumber: Int, val minorReleaseNumber: Int) {

    fun isOlder(otherVersion: Version): Boolean {
        return if (majorReleaseNumber < otherVersion.majorReleaseNumber) true else minorReleaseNumber < otherVersion.minorReleaseNumber
    }

    companion object {
        fun getVersionFromString(versionString: String): Version {
            val majorReleaseNumber = versionString.substring(0, versionString.indexOf(".")).toInt()
            val minorReleaseNumber = versionString.substring(versionString.indexOf(".") + 1).toInt()
            return Version(majorReleaseNumber, minorReleaseNumber)
        }
    }
}