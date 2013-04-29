<<<<<<< HEAD
=======
<<<<<<< HEAD
(defglobal
    ?*ACTION* = 0)
(defrule r1
    (health ?h&:(< ?h healthLowLimit))
    =>
    (assert (healthLevel Low))
    (retract health ?)
)

(defrule r2
    (health ?h&:(< ?h healthHighLimit))
    (health ?h&:(> ?h healthLowLimit))
    =>
    (assert (healthLevel Medium))
    (retract health ?)
)

(defrule r3
    (health ?h&:(< ?h healthHighLimit))
    =>
    (assert (healthLevel High))
    (retract health ?)
)

(defrule r4
    (armour ?h&:(< ?h armourLowLimit))
    =>
    (assert (armourLevel Low))
    (retract armour ?)
)

(defrule r5
    (armour ?a&:(< ?a armourhHighLimit))
    (armour ?a&:(> ?a armourLowLimit))
    =>
    (assert (armourLevel Medium))
    (retract armour ?)
)

(defrule r6
    (armour ?a&:(< ?h armourHighLimit))
    =>
    (assert (armourLevel High))
    (retract armour ?)
)

(defrule r7
    (health Low)
    =>
    (bind ?*ACTION* 1)
)

=======
>>>>>>> Alvaro
(defglobal
    ?*ACTION* = 0
)

(defrule r1
    (healthLowLimit ?lim)
    ?id <- (health ?h&:(< ?h ?lim))
    =>
<<<<<<< HEAD
    (printout t "Jess -> vida baja")
=======
>>>>>>> Alvaro
    (assert (healthLevel Low))
    (retract ?id)
)

(defrule r2
    (healthLowLimit ?lim)
    (healthHighLimit ?limH)
    (health ?h&:(< ?h ?lim))
    ?id <- (health ?h&:(> ?h ?limH))
    =>
<<<<<<< HEAD
    (printout t "Jess -> vida media")
=======
>>>>>>> Alvaro
    (assert (healthLevel Medium))
    (retract ?id)
)

(defrule r3
    (healthHighLimit ?limH)
    ?id <- (health ?h&:(> ?h ?limH))
    =>
<<<<<<< HEAD
    (printout t "Jess -> vida alta")
=======
>>>>>>> Alvaro
    (assert (healthLevel High))
    (retract ?id)
)



(defrule r7
    (healthLevel Low)
    =>
    (bind ?*ACTION* 1)
)

<<<<<<< HEAD
=======
>>>>>>> 2f584d718045bf8339d3b474c526f3575c0ec622
>>>>>>> Alvaro
