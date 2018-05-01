using Apache.NMS;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Communication
{
    class WrongMessageTypeException : Exception { }
    class UnknownMessageException : Exception { }

    abstract class Consumer
    {
        protected IMessageConsumer consumer;
        protected ISession session;

        public Consumer()
        {
            
        }

        protected abstract void Consumer_Listener(IMessage message);

        public virtual void Init(ISession session, String channelDestination)
        {
        }

        public void Terminate()
        {
            this.consumer.Listener -= Consumer_Listener;
            this.consumer = null;
            this.session = null;
        }
  
    }
}
