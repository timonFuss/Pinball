using Client.Communication;
using Client.Helper;
using System.Collections.ObjectModel;
using System;

namespace Client.ViewModels.UserControls
{
    class ClientlistViewModel : ViewModelBase
    {
        private MainProducer mainProducer;
        private MainConsumer mainConsumer;
        private ObservableCollection<string> _clientlist = new ObservableCollection<string>();

        public ObservableCollection<string> Clientlist
        {
            get { return _clientlist; }
            set { _clientlist = value; OnPropertyChanged("Clientlist"); }
        }

        public ClientlistViewModel(MainConsumer mainConsumer, MainProducer mainProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;

            mainConsumer.ClientLoginEvent += HandleClientLoginEvent;
            mainConsumer.ClientLogoutEvent += HandleClientLogoutEvent;
            mainConsumer.ClientsOnlineEvent += HandleClientsOnlineEvent;

            Clientlist.Add("hans");
            Clientlist.Add("peter");
        }

        private void HandleClientsOnlineEvent(string[] clientList)
        {
            foreach(string username in clientList)
            {
                _clientlist.Add(username);
            }
        }

        private void HandleClientLogoutEvent(string username)
        {
            _clientlist.Remove(username);
        }

        private void HandleClientLoginEvent(string username)
        {
            _clientlist.Add(username);
        }

    }
}
