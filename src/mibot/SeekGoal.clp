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

