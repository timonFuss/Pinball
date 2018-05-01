using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Communication
{
    class Command
    {
        /* Client to Server */
        public const string LOGIN = "login";                    // Loginwunsch
        public const string LOGOUT = "logout";                  // Logoutwunsch
        public const string WHOISON = "whoison";                // Interesse nach Liste aller Anwesenden

        // Editor + Game
        public const string WHICHFIELDS = "whichfields";            // Frage nach vorhanden Spielfeldern
        public const string WHICHOPENEDITORS = "whichopeneditors";	// Frage nach offenen beitretbaren Spielfeldern
	    public const string WHICHOPENGAMES = "whichopengames";		// Frage nach offenen beitretbaren Spielen
	
	    public const string STARTEDITOR = "starteditor";		// Eröffnen einer Editier-Sitzung
	    public const string STARTGAME = "startgame";			// Eröffnen einer Spiel-Sitzung
	    public const string JOINEDITOR = "joineditor";			// Beitritt zu einer Editier-Sitzung
	    public const string JOINGAME = "joingame";		    	// Beitritt zu einer Spiel-Sitzung

	
	    public const string COMMU_JOINED = "joinedcommunication"; 	// Client hat auf seiner Seite den Beitritt durchgeführt
	    public const string COMMU_LEFT = "leftcommunication"; 		// Client hat auf seiner Seite den Beitritt durchgeführt
	    public const string READYTOPLAY = "readytoplay";				// Manuelle Spielstartbestätigung
	    public const string LOADINGCOMPLETE = "loadingcomplete";        // (Spielfeld) laden fertig

        // Editor
        public const string SETELEMENT = "setelement";      // Nutzer versucht, Element zu setzen
        public const string MOVEELEMENT = "moveelement";    // Nutzer versucht, Element zu bewegen
        public const string DELETEELEMENT = "deleteelement";// Nutzer versucht, Element zu entfernen
        public const string UNDO = "undo";                  // Nutzer will letzten Schritt ungeschehen machen
        public const string REDO = "redo";                  // Nutzer will letzten Schritt doch wieder geschehen machen

        public const string CHANGENAME = "changename";      // Nutzer will Stringbezeichnung des Spielfelds anpassen
        public const string SAVEFIELD = "savefield";        //Nutzer will Editorfeld speichern

        // Game
        public const string DEBUG_FORCEVECTOR = "debug_forcevector";

        public const string BUTTON_UP = "button_up";
        public const string BUTTON_DOWN = "button_down";
        public const string BUTTON_LEFT = "button_left";
        public const string BUTTON_RIGHT = "button_right";

        public const string FLIPPER_LEFT = "flipper_left";
        public const string FLIPPER_RIGHT = "flipper_right";

        public const string POINTS = "points";

        /* Server to Client */
        // Main
        public const string CLIENTLOGIN = "clientlogin";        // Notification zu fremden Logins
        public const string CLIENTLOGOUT = "clientlogout";      // Notification zu fremden Logouts
        public const string LOGINOK = "loginok";                // Antwort auf Loginwunsch
        public const string CLIENTSONLINE = "clientsonline";    // Versand der Liste aller Anwesenden

        // Editor + Game (1:1)
        public const string AVAILABLEFIELDS = "availablefields";// vorhandene Spielfelder
	    public const string OPENEDITORS = "openeditors";	    // Offene beitretbare aktuelle Editiersessions
	    public const string OPENGAMES = "opengames";            // Offene beitretbare aktuelle Spiele
        public const string CLOSINGEDITOR = "closingeditor";    // Vorhandener Editierraum schliesst
        public const string CLOSINGGAME = "closinggame";		// Vorhandener Spielraum schliesst
	
	    public const string YOUREDITOR = "youreditor";	// Antwort zu Editier-Sitzungsanfrage
	    public const string YOURGAME = "yourgame";		// Antwort zu Spiel-Sitzungsanfrage

        // Editor (Topic)
        public const string EDITORLOADPACKET = "editorloadpacket";  // Editorpaket

        public const string ELEMENTSET = "elementset";          // Element wurde gesetzt
        public const string ELEMENTMOVED = "elementmoved";      // Element wurde verschoben
        public const string ELEMENTDELETED = "elementdeleted";  // Element wurde entfernt
        public const string NAMECHANGED = "namechanged";        // Spielfeld-Stringbezeichnung wurde angepasst

        public const string FIELDSAVED = "fieldsaved";          //Antwort des Servers zum Spiel Speichern

        // Game (Topic)
        public const string PLAYERREADYTOPLAY = "playerreadytoplay";	// Manuelle Spielstartbestätigung
	    public const string CLIENTSINGAME = "clientsingame";			// Liste aller Spieler in bestimmtem Raum
        public const string PLAYERNUMBERLIST = "playernumberlist";      // Liste aller Spieler und zugehoeriger Spielernummer
        public const string GAMEFIELDLOADPACKET = "gamefieldloadpacket";// Spielfeldpaket
	    public const string GAMEUPDATEPACKET = "gameupdatepacket";		// Updatepakete im Spiel

        public const string ELEMENTHIT = "elementhit";
	    public const string WALLHIT = "wallhit";
	    public const string FLIPPERMOVE = "flippermove";
        public const string SHOOTOUT = "shootout";

        public const string GAMESTARTS = "gamestarts";                  //Signal zu Spielende
        public const string GAMEOVER = "gameover";				        // Signal zu Spielende
        public const string NOREVANCHE = "norevanche";                  //Revanche wurde abgelehnt

    }

}
