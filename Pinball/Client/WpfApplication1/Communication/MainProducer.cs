using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;
using Apache.NMS.ActiveMQ.Commands;



namespace Client.Communication
{
    class MainProducer : Producer
    {

        public event ConnectionLost ConnectionLostEvent;

        public MainProducer()
        {
        }

        /// <summary>
        /// Verschicken einer Login-Anfrage
        /// </summary>
        /// <param name="username">Sichtbarer Nutzername, unter dem man zu sehen sein soll (muss auf Server einzigartig sein)</param>
        public void Login(String username)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.LOGIN;
                msg.Properties.SetString(Parameter.USERNAME, username);

                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// /// Verschicken einer Logout-Anfrage
        /// </summary>
        public void Logout()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.LOGOUT;

                PrepareMessage(msg);
           
                producer.Send(msg);
            }
            catch { }
            
        }

        /// <summary>
        /// Verschicken einer Anfrage nach einer Liste aller Spieler, die online sind
        /// </summary>
        public void ListRequest()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.WHOISON;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }

        }

        /// <summary>
        /// Verschicken einer Anfrage nach einer Lister aller betretbaren offenen Spiele
        /// </summary>
        public void AskForOpenGames()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.WHICHOPENGAMES;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Verschicken einer Spielbeitrittsanfrage
        /// </summary>
        /// <param name="gameid">Spielidentifikation des Wunschspiels</param>
        public void JoinGame(string gameid)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.JOINGAME;
                msg.Properties.SetString(Parameter.ROOM_ID, gameid);

                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Verschicken einer Spielbeitrittsanfrage für einen neu erstellten Spielraum
        /// </summary>
        /// <param name="field_id">Spielidentifikation des Wunschfeldes</param>
        public void StartGame(string field_id, string roomName, int time)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.STARTGAME;
                msg.Properties.SetString(Parameter.GAMEFIELDNAME, field_id);
                msg.Properties.SetString(Parameter.NEWROOMNAME, roomName);
                msg.Properties.SetInt(Parameter.GAMEDURATION, time);

                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Verschicken einer Anfrage nach einer Lister aller Editor-Räume
        /// </summary>
        public void AskForOpenEditors()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.WHICHOPENEDITORS;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Verschicken einer Anfrage nach einer Lister aller Spielfelder
        /// </summary>
        public void AskForGamefields()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.WHICHFIELDS;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Verschicken einer Editorbeitrittsanfrage
        /// </summary>
        /// <param name="editorid">Editoridentifikation des Wunsch-Editor-Raums</param>
        public void JoinEditor(string editorid)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.JOINEDITOR;
                msg.Properties.SetString(Parameter.ROOM_ID, editorid);

                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Verschicken einer Spielbeitrittsanfrage für einen neu erstellten Spielraum
        /// </summary>
        /// <param name="gameid">Spielidentifikation des Wunschspiels</param>
        public void StartEditor(string field_id, string roomName)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.STARTEDITOR;
                msg.Properties.SetString(Parameter.GAMEFIELDNAME, field_id);
                msg.Properties.SetString(Parameter.NEWROOMNAME, roomName);

                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }
    }
}
