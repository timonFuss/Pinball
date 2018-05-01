using Apache.NMS;
using Apache.NMS.ActiveMQ.Commands;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;


namespace Client.Communication
{
    class GameConsumer : Consumer
    {

        //Zeit ist abgelaufen und das Spiel ist beendet
        public delegate void GameOver(string playerName, float score);
        public event GameOver GameOverEvent;

        //Revancheoption nicht mehr moeglich
        public delegate void NoRevanche();
        public event NoRevanche NoRevancheEvent;

        //GameLoad-Events
        public delegate void SetPosX(int id, float posX);
        public event SetPosX SetPosXEvent;
        public delegate void SetPosY(int id, float posY);
        public event SetPosY SetPosYEvent;
        public delegate void SetType(int id, string type);
        public event SetType SetTypeEvent;
        public delegate void SetChangeable(int id, bool changeable);
        public event SetChangeable SetChangeableEvent;

        //Spiel (Timer) wird gestartet
        public delegate void GameStart(string name, int time);
        public event GameStart GameStartEvent;



        // wenn das GameLoadPacket fertig verarbeitet wurde
        public delegate void GameLoadMessageProcessComplete();
        public event GameLoadMessageProcessComplete GameLoadMessageProcessCompleteEvent;

        // Update-Events von Spielobjekten, um das Spielfeld zu aktualisieren
        public delegate void UpdatePosX(int id, float posX);
        public event UpdatePosX UpdatePosXEvent;
        public delegate void UpdatePosY(int id, float posY);
        public event UpdatePosY UpdatePosYEvent;
        public delegate void UpdateRotation(int id, float rotation);
        public event UpdateRotation UpdateRotationEvent;

        // Nachrichten für Effekte und Sound
        public delegate void ElementHit(int id);
        public event ElementHit ElementHitEvent;
        public delegate void WallHit();
        public event WallHit WallHitEvent;
        public delegate void FlipperMoved(bool up);
        public event FlipperMoved FlipperMovedEvent;
        public delegate void ShooutOut();
        public event ShooutOut ShooutOutEvent;


        // wenn der Client vom Server eine Liste der Spieler die mitspielen erhält
        public delegate void ClientsIngame(string[] clientList);
        public event ClientsIngame ClientsIngameEvent;

        // wenn ein Mitspieler auf 'Bereit zum Spielen' geklickt hat und der Server dies mitteilt
        public delegate void PlayerReadyToPlay(string username, bool isReady);
        public event PlayerReadyToPlay PlayerReadyToPlayEvent;

        // wenn zu Spielbeginn die Spielernummerverteilung vom Server mitgeteilt wird
        public delegate void PlayerNumberList(string username, int playerNumber);
        public event PlayerNumberList PlayerNumberListEvent;


        // wenn vom Server ein Score update kommt
        public delegate void NewScore(string username, int score);
        public event NewScore NewScoreEvent;

        public GameConsumer() { }

        public override void Init(ISession session, string channelDestination)
        {
            this.session = session;
            this.consumer = session.CreateConsumer(new ActiveMQTopic(channelDestination));
            consumer.Listener += Consumer_Listener;
        }

        protected override void Consumer_Listener(IMessage message)
        {
            ITextMessage textMessage = (ITextMessage)message;
            // der Text der TextMessage ist der Command, die Properties sind die parameter des Commands
            string command = textMessage.Text;
            //try
            //{
                // je nach command werden unterscheidliche Events ausgelöst mit den richtigen Parametern
                switch (command)
                {
                    case Command.CLIENTSINGAME:
                        ClientsIngameEvent(textMessage.Properties.GetString(Parameter.CLIENTLIST).Split(Parameter.LISTELEMENTSEPARATOR));
                        break;
                    case Command.PLAYERREADYTOPLAY:
                        foreach (string playerName in textMessage.Properties.Keys)
                        {
                            PlayerReadyToPlayEvent(playerName, textMessage.Properties.GetBool(playerName));
                        }
                        break;
                    case Command.PLAYERNUMBERLIST:
                        foreach (string playerName in textMessage.Properties.Keys)
                        {  
                            PlayerNumberListEvent(playerName, textMessage.Properties.GetInt(playerName));
                        }
                        break;
                case Command.POINTS:
                            NewScoreEvent(textMessage.Properties.GetString(Parameter.USERNAME),textMessage.Properties.GetInt(Parameter.POINTS));
                    break;

                case Command.GAMEFIELDLOADPACKET:
                        //Spielfeld erhalten und entschlüsseln
                        int loadID = 0;
                        foreach (string key in textMessage.Properties.Keys)
                        {
                            // LoadPaket auslesen und dem zu ladendem Objekt (dessen id) zuordnen
                            // Beispiel: key{int id:Parameter},value{Datentyp} --> key{3:POSX},value{5.3} --> Objekt 3 liegt auf x=5.3
                            loadID = int.Parse(key.Substring(0, key.IndexOf(Parameter.IDTYPESEPERATOR)));
                            if (key.Contains(Parameter.POSX) && textMessage.Properties[key].GetType() == typeof(float))
                                SetPosXEvent(loadID, textMessage.Properties.GetFloat(key));
                            if (key.Contains(Parameter.POSY) && textMessage.Properties[key].GetType() == typeof(float))
                                SetPosYEvent(loadID, textMessage.Properties.GetFloat(key));
                            if (key.Contains(Parameter.ELEMENTTYPE) && textMessage.Properties[key].GetType() == typeof(string))
                                SetTypeEvent(loadID, textMessage.Properties.GetString(key));
                            if (key.Contains(Parameter.CHANGEABLE) && textMessage.Properties[key].GetType() == typeof(bool))
                                SetChangeableEvent(loadID, textMessage.Properties.GetBool(key));
                        }

                        GameLoadMessageProcessCompleteEvent();
                        break;
                    case Command.GAMESTARTS:
                        GameStartEvent(textMessage.Properties.GetString(Parameter.ROOMNAME), textMessage.Properties.GetInt(Parameter.GAMEDURATION));
                        break;
                    case Command.GAMEUPDATEPACKET:
                        int id = 0;
                        foreach (string key in textMessage.Properties.Keys)
                        {
                            // Änderung auslesen und dem zu ändernden Objekt (dessen id) zuordnen
                            // Beispiel: key{int id:Parameter},value{Datentyp} --> key{3:POSX},value{5.3} --> Objekt 3 verschiebt sich auf x=5.3
                            id = int.Parse(key.Substring(0, key.IndexOf(Parameter.IDTYPESEPERATOR)));
                            if (key.Contains(Parameter.POSX) && textMessage.Properties[key].GetType() == typeof(float))
                                UpdatePosXEvent(id, textMessage.Properties.GetFloat(key));
                            if (key.Contains(Parameter.POSY) && textMessage.Properties[key].GetType() == typeof(float))
                                UpdatePosYEvent(id, textMessage.Properties.GetFloat(key));
                            if (key.Contains(Parameter.ROT) && textMessage.Properties[key].GetType() == typeof(float))
                                UpdateRotationEvent(id, textMessage.Properties.GetFloat(key));
                            // hier zukünftige Updatevariablen einfügen
                        }
                        break;
                    case Command.ELEMENTHIT:
                        ElementHitEvent(textMessage.Properties.GetInt(Parameter.ID));
                        break;
                    case Command.WALLHIT:
                        WallHitEvent();
                        break;
                    case Command.FLIPPERMOVE:
                        FlipperMovedEvent(textMessage.Properties.GetBool(Parameter.UP));
                        break;
                    case Command.SHOOTOUT:
                        // es kann vorkommen dass Shootout nach Spielende nochmal gesendet wird
                        try
                        {
                            ShooutOutEvent();
                        }
                        catch { }                        
                        break;
                case Command.GAMEOVER:
                        foreach (string playerName in textMessage.Properties.Keys)
                        {
                            GameOverEvent(playerName, textMessage.Properties.GetInt(playerName));
                        }
                        break;
                    case Command.NOREVANCHE:
                        NoRevancheEvent();
                        break;
                // hier zukünftige Commands einfügen

                default:
                        // wenn ein unbekannter Command oder falsche Parameter geschickt wurden:
                        throw new UnknownMessageException();
                }
            //}
            //catch (Exception e)
            //{
            //    Console.WriteLine("C#-Fehler bei der Verarbeitung der Commands " + command);
            //    Console.WriteLine(e.Message);
            //}
        }

    }
}

