using Apache.NMS;
using Apache.NMS.ActiveMQ.Commands;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Communication
{
    abstract class Producer
    {
        protected IMessageProducer producer;
        protected ISession session;
        protected IDestination responseQueue;
        protected const String DEFAULTCORRELATIONID = "id";

        public delegate void ConnectionLost();


        public Producer()
        {
           
        }

        /// <summary>
        /// Infos zur Identifikation des Senders (dieser Client) an Message heften
        /// </summary>
        /// <param name="msg">Zu verschickende Nachricht</param>
        protected void PrepareMessage(IMessage msg)
        {
            msg.NMSReplyTo = responseQueue;
            // id kann dazu dienen, Antworten auf Clientseite auseinander zu halten (muss dann differenzierter sein)
            // msg.NMSCorrelationID = DEFAULTCORRELATIONID;
        }

        public void Init(ISession session, String queueName, IDestination responseQueue)
        {
            this.session = session;
            // Producer zur Kommunikation in Richtung Server
            producer = session.CreateProducer(new ActiveMQQueue(queueName));
            // TemporaryQueue zur Antwort vom Server
            this.responseQueue = responseQueue;
        }

        public void Terminate()
        {
            this.producer = null;
            this.session = null;
            this.responseQueue = null;
        }
    }
}
