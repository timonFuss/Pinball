using Client.Communication;
using Client.Helper;
using Client.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Client.ViewModels.UserControls
{
    class GameMenuViewModel : ViewModelBase
    {

        private MainProducer mainProducer;
        private MainConsumer mainConsumer;

        private ObservableCollection<string> _clientlist = new ObservableCollection<string>();
        private ObservableCollection<GameButtonData> _gamelist = new ObservableCollection<GameButtonData>();

        
        public ObservableCollection<GameButtonData> Gamelist
        {
            get
            {
                return _gamelist;
            }
            set
            {
                _gamelist = value;
                OnPropertyChanged("Gamelist");
            }
        }
        public ObservableCollection<string> Clientlist
        {
            get { return _clientlist; }
            set { _clientlist = value; OnPropertyChanged("Clientlist"); }
        }

        public GameMenuViewModel(MainConsumer mainConsumer, MainProducer mainProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;


            mainConsumer.ClientLoginEvent += HandleClientLoginEvent;
            mainConsumer.ClientLogoutEvent += HandleClientLogoutEvent;
            mainConsumer.ClientsOnlineEvent += HandleClientsOnlineEvent;

            mainConsumer.OpenGamesEvent += HandleOpenGamesEvent;
            mainConsumer.ClosingGameEvent += HandleClosingGameEvent;

        }

        /// <summary>
        /// Anzeige aller Spiele, die gespielt werden koennen
        /// </summary>
        /// <param name="gameID"></param>
        /// <param name="player"></param>
        private void HandleOpenGamesEvent(string gameID, string roomName, int player)
        {
            // Suchen, ob Spiel in dieser Logik bereits als Button vorhanden
            foreach (GameButtonData gbd in Gamelist)
            {
                if (gbd.GameID.Equals(gameID))
                {
                    // Wenn gefunden Spielerzahl aktualisieren
                    gbd.NrPlayer = player;
                    return;
                }
            }

            // Wenn Spiel noch nicht vermerkt, ebendies tun
            GameButtonData button = new GameButtonData(gameID, roomName, player);
            button.JoinGameEvent += HandleJoinGameEvent;

            App.Current.Dispatcher.Invoke((Action)delegate
            {
                Gamelist.Add(button);
            });
            OnPropertyChanged("Gamelist");
   
        }


        /// <summary>
        /// Entfernen eines Raumbuttons
        /// </summary>
        /// <param name="gameID"></param>
        private void HandleClosingGameEvent(string gameID)
        {
            // Suchen, ob Spiel in dieser Logik bereits als Button vorhanden
            foreach (GameButtonData gbd in Gamelist)
            {
                if (gbd.GameID.Equals(gameID))
                {
                    // Wenn gefunden entfernen
                    App.Current.Dispatcher.Invoke((Action)delegate
                    {
                        Gamelist.Remove(gbd);
                    });
                }
            }
            OnPropertyChanged("Gamelist");

        }



        private void HandleJoinGameEvent(string id)
        {
            mainProducer.JoinGame(id);
        }

        /// <summary>
        /// Der Client erhaelt vom Server eine Liste aller Clients, die online sind. Diese werden in der Clientlist gespeichert.
        /// </summary>
        /// <param name="clientList"></param>
        private void HandleClientsOnlineEvent(string[] clientList)
        {
            foreach (string username in clientList)
            {
                if (username != "")
                {
                    App.Current.Dispatcher.Invoke((Action)delegate
                    {
                        Clientlist.Add(username);
                        OnPropertyChanged("Clientlist");
                    });
                }
            }
        }

        /// <summary>
        /// Falls sich ein Client ausgeloggt hat, wird er aus der Clientlist geloescht.
        /// </summary>
        /// <param name="username"></param>
        private void HandleClientLogoutEvent(string username)
        {
            App.Current.Dispatcher.Invoke((Action)delegate
            {
                Clientlist.Remove(username);
                OnPropertyChanged("Clientlist");
            });
        }

        private void HandleClientLoginEvent(string username)
        {
            App.Current.Dispatcher.Invoke((Action)delegate
            {
                Clientlist.Add(username);
                OnPropertyChanged("Clientlist");
            });
            
        }

        public delegate void BackToMenuCommandHandler();
        public event BackToMenuCommandHandler BackToMenuEvent;

        private ICommand _backCommand;
        public ICommand BackCommand
        {
            get
            {
                if (_backCommand == null)
                {

                    _backCommand = new ActionCommand(dummy => BackToMenuEvent(), null);
                }
                //return null?
                return _backCommand;
            }
        }

        public delegate void GameFieldSelectionCommandHandler();
        public event GameFieldSelectionCommandHandler ShowGameFieldSelectionViewEvent;
        private ICommand _newRoomCommand;

        public ICommand NewRoomCommand
        {
            get
            {
                if (_newRoomCommand == null)
                {

                    _newRoomCommand = new ActionCommand(dummy => OpenGamefields(), null); // NEUES SPIEL ERSTELLEN!!
                }
                return _newRoomCommand;
            }
        }

        private void OpenGamefields()
        {
            mainProducer.AskForGamefields();
            ShowGameFieldSelectionViewEvent();
        }
    }
}
