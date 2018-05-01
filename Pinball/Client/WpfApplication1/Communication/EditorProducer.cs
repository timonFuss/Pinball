using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;

namespace Client.Communication
{
    class EditorProducer : Producer
    {
        public event ConnectionLost ConnectionLostEvent;

        /// <summary>
        /// Verschicken einer Bestätigung, dass man die vom Server vorgeschlagenen Kommunikationswege nutzt
        /// </summary>
        public void ConfirmNewCommunication()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.COMMU_JOINED;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
            
        }

        /// <summary>
        /// Verschicke Info, dass man den Editor verlässt
        /// </summary>
        public void LeaveEditor()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.COMMU_LEFT;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Setze ein Element an eine Stelle
        /// </summary>
        public void SetElement(float posX, float posY, string elementType)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.SETELEMENT;
                msg.Properties.SetFloat(Parameter.POSX, posX);
                msg.Properties.SetFloat(Parameter.POSY, posY);
                msg.Properties.SetString(Parameter.ELEMENTTYPE, elementType);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// bewege ein vorhandenes Element an eine andere Stelle
        /// </summary>
        public void MoveElement(int id, float moveX, float moveY)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.MOVEELEMENT;
                msg.Properties.SetInt(Parameter.ID, id);
                msg.Properties.SetFloat(Parameter.POSX, moveX);
                msg.Properties.SetFloat(Parameter.POSY, moveY);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// lösche das Element mit der übergebenen ID
        /// </summary>
        public void DeleteElement(int id)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.DELETEELEMENT;
                msg.Properties.SetInt(Parameter.ID, id);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// mache den letzten auf dem Server ausgeführten Befehl rückgängig
        /// </summary>
        public void Undo()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.UNDO;
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// stelle den letzten auf dem Server ausgeführten Befehl wieder her
        /// </summary>
        public void Redo()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.REDO;
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// stelle den letzten auf dem Server ausgeführten Befehl wieder her
        /// </summary>
        public void ChangeName(string newGameFieldName)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.CHANGENAME;
                msg.Properties.SetString(Parameter.GAMEFIELDNAME, newGameFieldName);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Editor soll gespeichert werden
        /// </summary>
        public void SaveEditorGame()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.SAVEFIELD;
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }
    }
}
