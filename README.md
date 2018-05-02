# Pinball

<img src="https://user-images.githubusercontent.com/19949679/39542656-6dd57520-4e49-11e8-9a03-1309d87b3b91.PNG" width="250" height="250">


# Einleitung

Wir wollen einen Multiplayer Flipper erstellen, der auf verschiedenen Computern das Zusammenspiel ermöglicht.


# Sinn des Spiels

Es soll möglich sein, Teams zu bilden und mit bis zu vier Spielern auf einem Brett zu spielen. Dabei hat jeder Spieler seine eigenen Hebel, die er mit seiner Tastatur selbst steuern kann. Aus einem in der Mitte liegenden, um die eigene Achse rotierenden Bumper wird in eine zufällige Richtung eine Kugel ausgeworfen. Weitere Spielbausteine liegen auf dem Feld und kollidieren mit der Kugel. Die Kugel prallt an Hindernissen ab, erhöht die Geschwindigkeit oder wird verschluckt und erneut ausgestoßen, mit dem Ziel zwischen die Plunger der Spieler geschossen zu werden.

Die Spieler haben pro Spiel 2 Minuten Zeit so oft es geht zu versuchen mit den eigenen Hebeln oder dem Libero den Ball zwischen die gegnerischen Hebel zu schießen. In der Spiellobby ist es allerdings auch möglich die Spielzeit zu verkürzen auf 30 Sekunden oder 1 Minute. Bei jedem Kontakt zwischen Libero und Ball oder Hebel und Ball bekommt der Spieler Punkte (Hebel 10, Libero mindestens 3 je nach Länge der Berührung). Wenn der Ball allerdings zwischen die Hebel rollt, verliert der Spieler 50 Punkte. Vor jedem Flipperhebel eines Spielers liegt ein Strafraum. Der Strafraum behindert den Libero daran, zu nah an die Hebel zu kommen, um sich selbst einen Vorteil zu verschaffen. Es soll so im Spiel vor allem dazu animiert werden, dass der Spieler strategisch schlau seinen Libero und seine Hebel einsetzt. Wer am Ende die meisten Punkte hat, gewinnt das Spiel. Damit die Spieler nicht direkt in den negativen Punktezahlbereich kommen, geben wir jedem Spieler 100 Punkte vor.

Damit es nicht langweilig wird, können die Spieler aus verschiedenen Spielfeldern wählen, welches sie benutzen möchten. Diese werden auf dem Server gespeichert und sind jederzeit aufrufbar. Wird ein neues Spielbrett erstellt, kann er dieses auf dem Server abspeichern und dann bespielen.


# Ablauf

Beim Start des Spiels hat der Spieler die Möglichkeit entweder direkt ein Spiel zu starten oder sich ein eigenes Spielfeld zu erstellen. Wenn er auf Spielen geht, sieht der Spieler alle im Moment anwesenden Mitspieler. In einer Liste werden diese mit ihren Spielernamen aufgeführt. Zusätzlich sieht der Spieler alle offenen Spielen denen er beitreten kann. Dies sind Spiele, die andere Mitspieler hinzugefügt haben und auf Mitspieler warten. Hier sieht der Spieler auch pro Feld, wie viele Spieler bereits in dem Spiel angemeldet sind. Per Plus-Button-Klick kann der Spieler selbst ein neues Spielfeld eröffnen. Nach Auswahl des gewünschten Spielfelds würde dieses dann auch bei den offenen Spielen angezeigt werden. Hat sich der Spieler für ein Spiel entschieden kommt er in die Spiellobby in der die beigetretenen Spieler zu sehen sind. Wenn jeder Spieler sich bereit erklärt hat zu spielen, sehen die Mitspieler zum einen einen Haken an des Mitspielers Namen und zum andern können sie nichts mehr drücken, bis sich alle bereit erklärt haben. Dann beginnt das Spiel und jeder Spieler hat, wie oben beschrieben, die Möglichkeit, so viele Punkte wie möglich zu erreichen.

Am Ende des Spiels gibt es die Möglichkeit auf eine Revanche. Das heißt man kann das selbe Spielfeld in der selben Konstellation ohne große Mühe erneut spielen. Dazu muss nur jeder Spieler auf Revanche klicken und man gelangt direkt in die Spiellobby des Feldes.


# Ein neues Spielbrett konfigurieren

Es soll zudem möglich sein, dass Nutzer ihre eigenen Spielfelder aufbauen können oder aber bestehende Spielfelder editieren. Dazu bekommen sie eine Vorlage und eine Liste mit allen verfügbaren Bausteinen für einen Flipper. Der Nutzer kann sich nach Belieben die Steine auf das Spielfeld ziehen und so seine ganz eigene Kreation eines Flipper-Spiels erarbeiten. Seine Kreation kann er dann auf dem Server zwischenspeichern und später das Spielfeld laden und bespielen. Es gibt im Spielfeld bestimmte Bereiche, in denen kein Stein gesetzt werden kann, damit die Hebel nicht zugebaut werden können. Zudem ist initial auf dem Feld jeder Libero zu sehen. Im Spiel wird der Libero auch von dieser Position starten.

<img src="https://user-images.githubusercontent.com/19949679/39543096-95aea494-4e4a-11e8-8443-d494274cefd7.png" width="500" height="500">

Ein nettes und hilfreiches Feature sind dabei Hilfslinien, die man sich selbst ziehen kann. Aus der Seite können diese gezogen werden und auf dem Feld, je nach Belieben gezogen werden, um eine fast punktgenaue Symmetrie im Feld herzustellen.

