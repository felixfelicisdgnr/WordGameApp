package com.doganur.wordgameapp

data class LetterData(val value : String, val score : Int)

//harfler ve puanları
val letterList = listOf(
    LetterData("A",1),
    LetterData("B",2),
    LetterData("C",4),
    LetterData("Ç",4),
    LetterData("D",3),
    LetterData("E",1),
    LetterData("F",7),
    LetterData("G",5),
    LetterData("Ğ",8),
    LetterData("H",5),
    LetterData("I",2),
    LetterData("İ",1),
    LetterData("J",10),
    LetterData("K",1),
    LetterData("L",1),
    LetterData("M",2),
    LetterData("N",1),
    LetterData("O",2),
    LetterData("Ö",7),
    LetterData("P",5),
    LetterData("R",1),
    LetterData("S",2),
    LetterData("Ş",4),
    LetterData("T",1),
    LetterData("U",2),
    LetterData("Ü",3),
    LetterData("V",7),
    LetterData("Y",3),
    LetterData("Z",4)
)

