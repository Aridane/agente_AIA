(defglobal
    ?*ACTION* = 0
)

(defrule r1
    (healthLowLimit ?lim)
    ?id <- (health ?h&:(< ?h ?lim))
    =>
    (assert (healthLevel Low))
    (retract ?id)
)

(defrule r2
    (healthLowLimit ?lim)
    (healthHighLimit ?limH)
    (health ?h&:(< ?h ?lim))
    ?id <- (health ?h&:(> ?h ?limH))
    =>
    (assert (healthLevel Medium))
    (retract ?id)
)

(defrule r3
    (healthHighLimit ?limH)
    ?id <- (health ?h&:(> ?h ?limH))
    =>
    (assert (healthLevel High))
    (retract ?id)
)



(defrule r7
    (healthLevel Low)
    =>
    (bind ?*ACTION* 1)
)

