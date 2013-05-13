(defglobal
    ?*ACTION* = 0
)

;L
(defrule r1
    ?id1 <- (healthLevel LOW)
    =>
    (printout t "1" crlf)
    (bind ?*ACTION* 3)
)
;ML
(defrule r2
    ?id1 <- (healthLevel MED)
    ?id2 <- (armorLevel LOW)
    =>
    (printout t "2" crlf)
    (bind ?*ACTION* 3)
)
;MMDis
(defrule r3
    (healthLevel MED)
    (armorLevel MED)
    (advantage ?adv&:(< ?adv 0))
    =>
    (printout t "3" crlf)
    (bind ?*ACTION* 3)
)
;MMAdv
(defrule r4
    (healthLevel MED)
    (armorLevel MED)
    (advantage ?adv&:(> ?adv 0))
    =>
    (printout t "4" crlf)
    (bind ?*ACTION* 1)
)
;MH-2
(defrule r5
    (healthLevel MED)
    (armorLevel HIGH)
    (advantage -2)
    =>
    (printout t "5" crlf)
    (bind ?*ACTION* 3)
)
;MH-1
(defrule r6
    (healthLevel MED)
    (armorLevel HIGH)
    (advantage -2)
    =>
    (printout t "6" crlf)
    (bind ?*ACTION* 1)
)
;MHADV
(defrule r7
    (healthLevel MED)
    (armorLevel HIGH)
    (advantage ?adv&:(> ?adv -1))
    =>
    (printout t "7" crlf)
    (bind ?*ACTION* 1)
)
;HLDIS
(defrule r8
    (healthLevel HIGH)
    (armorLevel LOW)
    (advantage ?adv&:(< ?adv 1))
    
    =>
    (printout t "8" crlf)
    (bind ?*ACTION* 3)
)
;HLADV
(defrule r9
    (healthLevel HIGH)
    (armorLevel LOW)
    (advantage ?adv&:(> ?adv 0))
    =>
    (printout t "9" crlf)
    (bind ?*ACTION* 1)
)
;HM-2
(defrule r10
    (healthLevel HIGH)
    (armorLevel MED)
    (advantage -2)
    =>
    (printout t "10" crlf)
    (bind ?*ACTION* 3)
)
;HM>-2
(defrule r11
    (healthLevel HIGH)
    (armorLevel MED)
    (advantage ?adv&:(> ?adv -2))
    =>
    (printout t "11" crlf)
    (bind ?*ACTION* 1)
)
;HH
(defrule r12
    (healthLevel HIGH)
    (armorLevel HIGH)
    (advantage ?adv&:(> ?adv -2))
    =>
    (printout t "12" crlf)
    (bind ?*ACTION* 1)
)