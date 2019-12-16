package com.steamedbunx.android.whathaveidone

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import com.steamedbunx.android.whathaveidone.dataModel.WeightedString

class SearchUtils private constructor() {

    companion object {
        private val instance = SearchUtils()

        fun getInstence(): SearchUtils {
            return instance
        }
    }


    fun getNormalMatchWeightedString(matchPhrase: CharSequence, inputString: String): WeightedString? {
        // process the text for searching, ignoring spaces completely
        //val searchPhrase = matchPhrase.trim().replace("\\s".toRegex(), "").toLowerCase()
        val searchPhrase = matchPhrase
        val resultStringForNormalResult = SpannableString(inputString)

        var currentIndexForNormalMatch = 0

        // i need keep track of the index, so when a matched char is find
        // I can bold that character
        // this iterate through the entire string once
        for (i in inputString.indices) {
            // we find a match
            if (inputString[i] == searchPhrase[currentIndexForNormalMatch]) {
                // bold the character
                resultStringForNormalResult.setSpan(
                    StyleSpan(Typeface.BOLD),
                    i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // let index move up
                currentIndexForNormalMatch++
                // we find everything, in order
                if (currentIndexForNormalMatch >= searchPhrase.length) {
                    // this is a normal result with no pattern
                    return WeightedString(resultStringForNormalResult, 0)
                }
            }
        }
        // result not find after the entire iteration, pattern does not match
        return null
    }

    fun searchListForResults(searchPhrase: String, inputs: List<String>): List<WeightedString> {
        val result = ArrayList<WeightedString>()
        val normalizedSearchPhrase = searchPhrase.trim().replace("[^a-zA-Z]".toRegex(), "").toLowerCase()
        if (!normalizedSearchPhrase.isBlank()) {
            inputs.forEach {
                val potentialResult = getNormalMatchWeightedString(normalizedSearchPhrase, it)
                if (potentialResult != null) {
                    result.add(potentialResult)
                }
            }
        }
        return result.toList()
    }

}