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
    (bind ?*ACTION* 1)
)

