(defglobal
    ?*ACTION* = 0
)

(defrule r1
    (healthLowLimit ?lim)
    ?id <- (health ?h&:(< ?h ?lim))
    =>
    (printout t "Jess -> vida baja")
    (assert (healthLevel Low))
    (retract ?id)
)

(defrule r2
    (healthLowLimit ?lim)
    (healthHighLimit ?limH)
    (health ?h&:(< ?h ?lim))
    ?id <- (health ?h&:(> ?h ?limH))
    =>
    (printout t "Jess -> vida media")
    (assert (healthLevel Medium))
    (retract ?id)
)

(defrule r3
    (healthHighLimit ?limH)
    ?id <- (health ?h&:(> ?h ?limH))
    =>
    (printout t "Jess -> vida alta")
    (assert (healthLevel High))
    (retract ?id)
)



(defrule r7
    (healthLevel Low)
    =>
    (bind ?*ACTION* 3)
)
(defrule r8
    (healthLevel High)
    (ammo ?amm&:(> ?amm 50))
    (weapon ?w&:(> ?w 50))
    
    =>
    (bind ?*ACTION* 1)
)
(defrule r9
	(healthLevel Medium)
	(armourLevel Medium)
    (ammo ?amm&:(> ?amm 50))
    (weapon ?w&:(> ?w 50))
    
    =>
    (bind ?*ACTION* 1)
)

(defrule r10
	(healthLevel High)
    =>
    (bind ?*ACTION* 1)
)



