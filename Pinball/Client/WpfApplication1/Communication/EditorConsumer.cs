using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;
using Apache.NMS.ActiveMQ.Commands;

namespace Client.Communication
{
    class EditorConsumer : Consumer
    {
        // das Paket, das man am Anfang bekommt und die bisherige Map enthält
        public delegate void SetPosX(int id, float posX);
        public event SetPosX SetPosXEvent;
        public delegate void SetPosY(int id, float posY);
        public event SetPosY SetPosYEvent;
        public delegate void SetType(int id, string type);
        public event SetType SetTypeEvent;

        // wenn das EditorLoadPacket fertig verarbeitet wurde
        public delegate void EditorLoadMessageProcessComplete();
        public event EditorLoadMessageProcessComplete EditorLoadMessageProcessCompleteEvent;

        // Element gesetzt
        public delegate void ElementSet(int id, float posX, float posY, string elementType);
        public event ElementSet ElementSetEvent;

        // Element bewegt
        public delegate void ElementMoved(int id, float posX, float posY);
        public event ElementMoved ElementMovedEvent;

        // Element bewegt
        public delegate void ElementDeleted(int id);
        public event ElementDeleted ElementDeletedEvent;

        // Element bewegt
        public delegate void NameChanged(string newName);
        public event NameChanged NameChangedEvent;

        // wenn der Client vom Server eine Liste der Spieler die miteditieren erhält
        public delegate void ClientsInEditor(string[] clientList);
        public event ClientsInEditor ClientsInEditorEvent;

        public delegate void FieldSaved();
        public event FieldSaved FieldSavedEvent;




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
                case Command.ELEMENTSET:
                    ElementSetEvent(textMessage.Properties.GetInt(Parameter.ID), textMessage.Properties.GetFloat(Parameter.POSX), textMessage.Properties.GetFloat(Parameter.POSY), textMessage.Properties.GetString(Parameter.ELEMENTTYPE));
                    break;
                case Command.ELEMENTMOVED:
                    ElementMovedEvent(textMessage.Properties.GetInt(Parameter.ID), textMessage.Properties.GetFloat(Parameter.POSX), textMessage.Properties.GetFloat(Parameter.POSY));
                    break;
                case Command.ELEMENTDELETED:
                    ElementDeletedEvent(textMessage.Properties.GetInt(Parameter.ID));
                    break;
                case Command.NAMECHANGED:
                    NameChangedEvent(textMessage.Properties.GetString(Parameter.GAMEFIELDNAME));
                    break;
                case Command.EDITORLOADPACKET:
                    int id = 0;
                    foreach (string key in textMessage.Properties.Keys)
                    {
                        // Änderung auslesen und dem zu ändernden Objekt (dessen id) zuordnen
                        // Beispiel: key{int id:Parameter},value{Datentyp} --> key{3:POSX},value{5.3} --> Objekt 3 verschiebt sich auf x=5.3
                        id = int.Parse(key.Substring(0, key.IndexOf(Parameter.IDTYPESEPERATOR)));
                        if (key.Contains(Parameter.POSX) && textMessage.Properties[key].GetType() == typeof(float))
                            SetPosXEvent(id, textMessage.Properties.GetFloat(key));
                        if (key.Contains(Parameter.POSY) && textMessage.Properties[key].GetType() == typeof(float))
                            SetPosYEvent(id, textMessage.Properties.GetFloat(key));
                        if (key.Contains(Parameter.ELEMENTTYPE) && textMessage.Properties[key].GetType() == typeof(string))
                            SetTypeEvent(id, textMessage.Properties.GetString(key));
                        // hier zukünftige Updatevariablen einfügen
                    }
                    // signalisieren, dass das Paket verarbeitet wurde
                    EditorLoadMessageProcessCompleteEvent();
                    break;
                case Command.CLIENTSINGAME:
                    ClientsInEditorEvent(textMessage.Properties.GetString(Parameter.CLIENTLIST).Split(Parameter.LISTELEMENTSEPARATOR));
                    break;
                case Command.FIELDSAVED:
                    FieldSavedEvent();
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
