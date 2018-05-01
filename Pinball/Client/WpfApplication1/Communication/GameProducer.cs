using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;

namespace Client.Communication
{
    class GameProducer : Producer
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
        /// Benachrichtigen, dass man die vom Server zuletzt vorgeschlagenen Kommunikationswege nicht mehr nutzt
        /// </summary>
        public void LeaveCommunication()
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
        /// Benachrichtigen, dass der Nutzer sich zum Spielen bereit fühlt
        /// </summary>
        public void SignalReadyToPlay()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.READYTOPLAY;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        /// <summary>
        /// Benachrichtigen, dass der initiale Ladevorgang des Spiels abgeschlossen wurde
        /// </summary>
        public void SignalLoadingComplete()
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.LOADINGCOMPLETE;
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        public void AddForce(float x, float y)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.DEBUG_FORCEVECTOR;
                msg.Properties.SetFloat(Parameter.POSX, x);
                msg.Properties.SetFloat(Parameter.POSY, y);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        //W-Taste Interaktion
        public void buttonUp(bool keyAction)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.BUTTON_UP;
                msg.Properties.SetBool(Parameter.BUTTON_UP, keyAction);
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        //S-Taste Interaktion
        public void buttonDown(bool keyAction)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.BUTTON_DOWN;
                msg.Properties.SetBool(Parameter.BUTTON_DOWN, keyAction);
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        //A-Taste Interaktion
        public void buttonLeft(bool keyAction)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.BUTTON_LEFT;
                msg.Properties.SetBool(Parameter.BUTTON_LEFT, keyAction);
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        //D-Taste Interaktion
        public void buttonRight(bool keyAction)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.BUTTON_RIGHT;
                msg.Properties.SetBool(Parameter.BUTTON_RIGHT, keyAction);
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        //Methode zum Signalisieren das K Taste für Linken flipper gedrückt wurde
        public void leftFlipper(bool keyAction)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.FLIPPER_LEFT;
                msg.Properties.SetBool(Parameter.FLIPPER_LEFT, keyAction);
                PrepareMessage(msg);
                producer.Send(msg);
            }
            catch
            {
                ConnectionLostEvent();
            }
        }

        //Methode zum Signalisieren das L Taste für rechter flipper gedrückt wurde
        public void rightFlipper(bool keyAction)
        {
            try
            {
                ITextMessage msg = session.CreateTextMessage();
                msg.Text = Command.FLIPPER_RIGHT;
                msg.Properties.SetBool(Parameter.FLIPPER_RIGHT, keyAction);
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
