using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Communication
{
    class Parameter
    {
        //Main
        public const string USERNAME = "username";          // Nutzername
        public const string SUCCESS = "success";            // Bestätigung/Ablehnung
        public const string CLIENTLIST = "clientlist";      // Liste aller anwesenden Nutzer

        public const string FIELDLIST = "fieldlist";			// Liste aller nutzbaren Spielfelder
        public const string ROOM_ID = "roomid";					// "Raum", z.B. Spiel 17, Editor 3 (Protokoll hierfür kann abweichen!)
        public const string CLIENT_RCV_FROM = "clientrcvfrom";  // Weg, über den zukünftig der Client empfangen soll
        public const string CLIENT_SND_TO = "clientsendto";     // Weg, über den zukünftig der Client senden soll

        public const string NEWROOMNAME = "newroomname";        //Raumname

        // Editor
        public const string GAMEFIELDNAME = "gamefieldname";		// Spielfeldname
        public const string ID = "id";                              // Spielfeldname

        // Editor + Game
        public const string POSX = "posx";
	    public const string POSY = "posy";
        public const string ELEMENTTYPE = "elementtype";

        // Game
        public const string ROOMNAME = "roomname";
        public const string GAMEDURATION = "gameduration";

	    public const string ROT = "rot";
        public const string CHANGEABLE = "changeable";  // entscheidet, ob Spielelement von update-Methode beachtet

        public const string UP = "up"; // wurde flipper up oder flipper down gedrückt? (für sounds)


        public const string BUTTON_UP = "button_up";
        public const string BUTTON_DOWN = "button_down";
        public const string BUTTON_LEFT = "button_left";
        public const string BUTTON_RIGHT = "button_right";

        public const string FLIPPER_LEFT = "flipper_left";
        public const string FLIPPER_RIGHT = "flipper_right";

        public const string POINTS = "points";

        // Trennzeichen bei Stringlisten - Inhalte dürfen dieses nicht enthalten!
        public const char LISTELEMENTSEPARATOR = ';';
        public const char IDTYPESEPERATOR = ':';
    }
}
