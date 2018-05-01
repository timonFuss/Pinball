using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;
using Apache.NMS.ActiveMQ.Commands;

namespace Client.Communication
{
    // kümmert sich darum, Nachrichten die auf der MainQueue gesendet werden entgegenzunehmen, zu decodieren und passende Events auszulösen
    // Client-Logik kann sich an die passenden Events anmelden, um benachrichtigt zu werden
    class MainConsumer : Consumer
    {
        public MainConsumer() { }

        public override void Init(ISession session, String channelDestination)
        {
            this.session = session;
            this.consumer = session.CreateConsumer(new ActiveMQQueue(channelDestination));
            consumer.Listener += Consumer_Listener;
        }

        public void Init(ISession session, IDestination channelDestination)
        {
            this.session = session;
            this.consumer = session.CreateConsumer(channelDestination);
            consumer.Listener += Consumer_Listener;
        }

        // signalisiert, ob der eigene Login erfolgreich war
        public delegate void LoginOk (bool isOk);
        public event LoginOk LoginOkEvent;

        // wenn ein anderer Client online geht
        public delegate void ClientLogin(string username);
        public event ClientLogin ClientLoginEvent;

        // wenn ein anderer Client offline geht
        public delegate void ClientLogout(string username);
        public event ClientLogout ClientLogoutEvent;

        // wenn der Client den Server nach der Liste der Spieler fragt, die online sind und eine Antwort erhält
        public delegate void ClientsOnline(string [] clientList);
        public event ClientsOnline ClientsOnlineEvent;

        // wenn der Client den Server nach der Liste der Spielfelder fragt, die nutzbar sind und eine Antwort erhält
        public delegate void AvailableFields(string[] fieldList);
        public event AvailableFields AvailableFieldsEvent;

        // wenn der Client vom Server eine Schliessinfo erhaelt
        public delegate void ClosingGame(string gameID);
        public event ClosingGame ClosingGameEvent;

        // wenn der Client vom Server eine Schliessinfo erhaelt
        public delegate void ClosingEditor(string editorID);
        public event ClosingEditor ClosingEditorEvent;

        // wenn der Client vom Server (ggf. aktualisierte) Infos zu verfügbaren offenen Spielen erhält
        public delegate void OpenGames(string gameID, string roomName, int playerCount);
        public event OpenGames OpenGamesEvent;

        // wenn der Client vom Server eine Spielbeitrittsbestätigung mit neuen Kommunikationswegen erhält
        public delegate void GameLobbyEntry(string receiveFromID, string sendToID, string roomName, string fieldName);
        public event GameLobbyEntry GameLobbyEntryEvent;

        // wenn der Client vom Server eine Spielbeitrittsverweigerung erhält
        public delegate void GameLobbyEntryDenied();
        public event GameLobbyEntryDenied GameLobbyEntryDeniedEvent;

        //Editor
        // wenn der Client vom Server(ggf.aktualisierte) Infos zu offenen Editoren erhält
        public delegate void OpenEditors(string editorID, string roomName, int playerCount);
        public event OpenEditors OpenEditorsEvent;

        // wenn der Client vom Server eine Spielbeitrittsbestätigung mit neuen Kommunikationswegen erhält
        public delegate void EditorEntry(string receiveFromID, string sendToID, string roomName, string fieldName);
        public event EditorEntry EditorEntryEvent;

        // 
        protected override void Consumer_Listener(IMessage message)
        {
            // wir versenden sämtliche Nachrichten per TextMessage, andere Messagetypen resultieren aus einem Programmierfehler!
            if (!(message is ITextMessage))
			    throw new WrongMessageTypeException();

            ITextMessage textMessage = (ITextMessage)message;
            // der Text der TextMessage ist der Command, die Properties sind die parameter des Commands
            string command = textMessage.Text;
            try
            {
                // je nach command werden unterscheidliche Events ausgelöst mit den richtigen Parametern
                switch (command)
                {
                    case Command.LOGINOK:
                        LoginOkEvent(textMessage.Properties.GetBool(Parameter.SUCCESS));
                        break;
                    case Command.CLIENTLOGIN:
                        ClientLoginEvent(textMessage.Properties.GetString(Parameter.USERNAME));
                        break;
                    case Command.CLIENTLOGOUT:
                        ClientLogoutEvent(textMessage.Properties.GetString(Parameter.USERNAME));
                        break;
                    case Command.CLIENTSONLINE:
                        ClientsOnlineEvent(textMessage.Properties.GetString(Parameter.CLIENTLIST).Split(Parameter.LISTELEMENTSEPARATOR));
                        break;

                    case Command.AVAILABLEFIELDS:
                        string [] fl = textMessage.Properties.GetString(Parameter.FIELDLIST).Split(Parameter.LISTELEMENTSEPARATOR);
                        AvailableFieldsEvent(fl);
                        break;


                    /* Editor */
                    case Command.CLOSINGEDITOR:
                        ClosingEditorEvent(textMessage.Properties.GetString(Parameter.ROOM_ID));
                        break;

                    case Command.OPENEDITORS:
                        foreach (string editor in textMessage.Properties.Keys)
                        {
                            string[] ed = editor.Split(Parameter.LISTELEMENTSEPARATOR);
                            OpenEditorsEvent(ed[0], ed[1], textMessage.Properties.GetInt(editor));
                        }
                        break;
                    case Command.YOUREDITOR:
                        if (textMessage.Properties.GetBool(Parameter.SUCCESS))
                        {
                            EditorEntryEvent(textMessage.Properties.GetString(Parameter.CLIENT_RCV_FROM),
                                             textMessage.Properties.GetString(Parameter.CLIENT_SND_TO),
                                             textMessage.Properties.GetString(Parameter.ROOMNAME),
                                             textMessage.Properties.GetString(Parameter.GAMEFIELDNAME));
                        }
                        break;


                    /* Game */
                    case Command.CLOSINGGAME:
                        ClosingGameEvent(textMessage.Properties.GetString(Parameter.ROOM_ID));
                        break;

                    case Command.OPENGAMES: //beinhaltet immer Liste mit allen games
                        foreach (string game in textMessage.Properties.Keys)
                        {
                            string[] g = game.Split(Parameter.LISTELEMENTSEPARATOR);
                            OpenGamesEvent(g[0], g[1], textMessage.Properties.GetInt(game));
                        }
                        break;
                    case Command.YOURGAME:
                        if (textMessage.Properties.GetBool(Parameter.SUCCESS))
                        {
                            GameLobbyEntryEvent(textMessage.Properties.GetString(Parameter.CLIENT_RCV_FROM),
                                                textMessage.Properties.GetString(Parameter.CLIENT_SND_TO),
                                                textMessage.Properties.GetString(Parameter.ROOMNAME),
                                                textMessage.Properties.GetString(Parameter.GAMEFIELDNAME));
                        }
                        else
                        {
                            GameLobbyEntryDeniedEvent();
                        }
                        break;

                    // hier zukünftige Commands einfügen

                    default:
                        // wenn ein unbekannter Command oder falsche Parameter geschickt wurden:
                        throw new UnknownMessageException();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("C#-Fehler bei der Verarbeitung der Commands " + command);
                Console.WriteLine(e.Message);
            }

        }
    }
}
