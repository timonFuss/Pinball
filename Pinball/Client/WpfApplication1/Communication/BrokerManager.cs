using Apache.NMS;
using Apache.NMS.ActiveMQ;
using Apache.NMS.ActiveMQ.Commands;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Communication
{
    class BrokerManager
    {

        private static BrokerManager instance;
        private static bool initialized = false;
        const string TOPIC_NAME = "SPDefaultTopic";

        private IConnection connection;
        private ISession session;
        private IDestination clientDestination;
        private string ip;
        private const string typeTCP = "tcp://";
        private const string port = ":1618";

        


        private BrokerManager() { }

        public void Init(string ip)
        {
            if (initialized) return;
            this.ip = ip;
            IConnectionFactory connectionFactory = new ConnectionFactory(typeTCP + ip + port);
            connection = connectionFactory.CreateConnection();
            session = connection.CreateSession(AcknowledgementMode.AutoAcknowledge);
            clientDestination = session.CreateTemporaryQueue();
            initialized = true;
        }

        public static BrokerManager Instance
        {
            get
            {
                if(instance==null)
                {
                    instance = new BrokerManager();
                }
                return instance;
            }
        }

        public void StartConnection()
        {
            try
            {
                connection.Start();
            }
            catch(Exception)
            {
            }            
        }

        public ISession GetSession()
        {
            return session;
        }

        public IDestination GetClientDestination()
        {
            return clientDestination;
        }

        /// <summary>
        /// Wird aufgerufen, wenn die alte Verbindung abgebrochen wurde und macht den broker wieder initialisierbar
        /// </summary>
        public void ConnectionLost()
        {
            initialized = false;
        }

        /// <summary>
        /// Stoppt Connection und schließt Session und Connection, beendet sauber die Verbindung zum Server
        /// </summary>
        public void CloseConnection()
        {
            try
            {
                connection.Stop();
                session.Close();
                connection.Close();
            }
            catch { }           
        }
    }
}
