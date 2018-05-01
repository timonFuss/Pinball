# Pinball

# Einleitung

Wir wollen einen Multiplayer Flipper erstellen, der auf verschiedenen Computern das Zusammenspiel ermöglicht.


# Sinn des Spiels

Es soll möglich sein, Teams zu bilden und mit bis zu vier Spielern auf einem Brett zu spielen. Dabei hat jeder Spieler seine eigenen Hebel, die er mit seiner Tastatur selbst steuern kann. Aus einem in der Mitte liegenden, um die eigene Achse rotierenden Bumper wird in eine zufällige Richtung eine Kugel ausgeworfen. Weitere Spielbausteine liegen auf dem Feld und kollidieren mit der Kugel. Die Kugel prallt an Hindernissen ab, erhöht die Geschwindigkeit oder wird verschluckt und erneut ausgestoßen, mit dem Ziel zwischen die Plunger der Spieler geschossen zu werden.

Die Spieler haben pro Spiel 2 Minuten Zeit so oft es geht zu versuchen mit den eigenen Hebeln oder dem Libero den Ball zwischen die gegnerischen Hebel zu schießen. In der Spiellobby ist es allerdings auch möglich die Spielzeit zu verkürzen auf 30 Sekunden oder 1 Minute. Bei jedem Kontakt zwischen Libero und Ball oder Hebel und Ball bekommt der Spieler Punkte (Hebel 10, Libero mindestens 3 je nach Länge der Berührung). Wenn der Ball allerdings zwischen die Hebel rollt, verliert der Spieler 50 Punkte. Vor jedem Flipperhebel eines Spielers liegt ein Strafraum. Der Strafraum behindert den Libero daran, zu nah an die Hebel zu kommen, um sich selbst einen Vorteil zu verschaffen. Es soll so im Spiel vor allem dazu animiert werden, dass der Spieler strategisch schlau seinen Libero und seine Hebel einsetzt. Wer am Ende die meisten Punkte hat, gewinnt das Spiel. Damit die Spieler nicht direkt in den negativen Punktezahlbereich kommen, geben wir jedem Spieler 100 Punkte vor.

Damit es nicht langweilig wird, können die Spieler aus verschiedenen Spielfeldern wählen, welches sie benutzen möchten. Diese werden auf dem Server gespeichert und sind jederzeit aufrufbar. Wird ein neues Spielbrett erstellt, kann er dieses auf dem Server abspeichern und dann bespielen.
