package com.example.myapplication.db

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.io.InputStreamReader

class XmlErrorDatabase(
    private val inputStream: InputStream
) : ErrorDatabase {
    override fun findErrorDetailsByCode(errorCode: String): ErrorDetails? {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(InputStreamReader(inputStream.apply { reset() }))
        }
        parser.next()
        parser.require(XmlPullParser.START_TAG, null, "database")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "code" && parser.getAttributeValue(null, "value") == errorCode) {
                return readCodeTag(parser)
            } else {
                skipTag(parser)
            }
        }

        return null
    }


    private fun readCodeTag(parser: XmlPullParser): ErrorDetails {
        lateinit var description: String
        lateinit var fixSteps: List<String>
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            when (parser.name) {
                "description" -> {
                    description = readDescriptionTag(parser)
                }
                "fixSteps" -> {
                    fixSteps = readFixStepsTag(parser)
                }

            }
        }
        return ErrorDetails(
            description = description,
            fixSteps = fixSteps
        )
    }

    private fun readFixStepsTag(parser: XmlPullParser): List<String> {
        val result = mutableListOf<String>()

        parser.require(XmlPullParser.START_TAG, null, "fixSteps")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "step") {
                result.add(readStepTag(parser))
            }
        }
        parser.require(XmlPullParser.END_TAG, null, "fixSteps")

        return result
    }

    private fun readStepTag(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "step")
        val text = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, null, "step")
        return text
    }

    private fun readDescriptionTag(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "description")
        val text = readTagText(parser)
        parser.require(XmlPullParser.END_TAG, null, "description")
        return text
    }

    private fun readTagText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    private fun skipTag(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
