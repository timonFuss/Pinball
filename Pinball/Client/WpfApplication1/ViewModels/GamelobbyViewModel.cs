using Client.Communication;
using Client.Helper;
using Client.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows;
using System.Windows.Input;

namespace Client.ViewModels.UserControls
{
    class GameLobbyViewModel:ViewModelBase
    {

        private ObservableCollection<PlayerReadyData> _clientlist = new ObservableCollection<PlayerReadyData>();

        private MainProducer mainProducer;
        private MainConsumer mainConsumer;
        private GameConsumer gameConsumer;
        private GameProducer gameProducer;
        private Visibility _readyToPlayButton = Visibility.Visible;
        private Visibility _backButton = Visibility.Visible;
        private Visibility _revancheNotPossibleWarning = Visibility.Hidden;
        private string _roomName = "";
        private string _fieldName = "";

        public Visibility RevancheNotPossibleWarning
        {
            get
            {
                return _revancheNotPossibleWarning;
            }
            set
            {
                _revancheNotPossibleWarning = value;
                OnPropertyChanged("RevancheNotPossibleWarning");
            }
        }

        public Visibility BackButton
        {
            get
            {
                return _backButton;
            }
            set
            {
                _backButton = value;
                OnPropertyChanged("BackButton");
            }
        }

        public Visibility ReadyToPlayButton
        {
            get
            {
                return _readyToPlayButton;
            }
            set
            {
                _readyToPlayButton = value;
                OnPropertyChanged("ReadyToPlayButton");
            }
        }

        public string RoomName
        {
            get
            {
                return _roomName;
            }
            set
            {
                _roomName = value;
                OnPropertyChanged("GameInfo");
            }
        }

        public string FieldName
        {
            get
            {
                return _fieldName;
            }
            set
            {
                _fieldName = value;
                OnPropertyChanged("GameInfo");
            }
        }

        public string GameInfo
        {
            get
            {
                return _roomName + " (" + _fieldName + ")";
            }
        }

        public ObservableCollection<PlayerReadyData> Clientlist
        {
            get
            {
                return _clientlist;
            }
            set
            {
                OnPropertyChanged("Clientlist");
            }
        }

        /// <summary>
        /// zurueck zum GameMenu
        /// </summary>
        public delegate void StepBackCommandHandler();
        public event StepBackCommandHandler StepBackEvent;

        private ICommand _backCommand;
        public ICommand BackCommand
        {
            get
            {
                if (_backCommand == null)
                {

                    _backCommand = new ActionCommand(dummy => StepBack(), null);
                }
                return _backCommand;
            }
        }

        /// <summary>
        /// einen Schritt zurueck ins Hauptmenue und vom Spielraum abmelden
        /// </summary>
        private void StepBack()
        {
            // Beim Server vom Spielraum abmelden
            // und Kommunikationswege beenden
            gameConsumer.Terminate();
            gameProducer.LeaveCommunication();
            gameProducer.Terminate();

            // Screen wechseln
            StepBackEvent();

        }


        /// <summary>
        /// Client ist bereit zu spielen. gameProducer versendet Protokoll an Server
        /// </summary>
        private ICommand _startGameCommand;
        public ICommand StartGameCommand
        {
            get
            {
                if (_startGameCommand == null)
                {
                    _startGameCommand = new ActionCommand(dummy => StartGame(), null);
                }
                return _startGameCommand;
            }
        }

        private void StartGame()
        {
            BackButton = Visibility.Hidden;
            ReadyToPlayButton = Visibility.Hidden;
            gameProducer.SignalReadyToPlay();
        }


        public GameLobbyViewModel(MainConsumer mainConsumer, MainProducer mainProducer, GameConsumer gameConsumer, GameProducer gameProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;
            this.gameConsumer = gameConsumer;
            this.gameProducer = gameProducer;

            mainConsumer.GameLobbyEntryEvent += InitGameCommunication;
            gameConsumer.ClientsIngameEvent += HandleClientsInGame;
            gameConsumer.PlayerReadyToPlayEvent += HandlePlayerReadyToPlay;
            gameConsumer.NoRevancheEvent += HandleNoRevancheEvent;
        }

        public void reset()
        {
            ReadyToPlayButton = Visibility.Visible;
            BackButton = Visibility.Visible;
            RevancheNotPossibleWarning = Visibility.Hidden;
        }

        public void ResetPlayerLobbyList()
        {
            foreach (PlayerReadyData client in Clientlist)
            {
                 client.Visibility = "Hidden";
            }
            //Spieler Highlighten, die bereit sind
        }

        public void ClearPlayerLobbyList()
        {
            App.Current.Dispatcher.Invoke((Action)delegate
            {
                Clientlist.Clear();
            });
        }

        /// <summary>
        /// Revanche nicht mehr umsetzbar. Spiel nicht startbar.
        /// Lobby verlassen bzw. zum Verlassen auffordern.
        /// </summary>
        private void HandleNoRevancheEvent()
        {
            // Spielbereit-Button deaktivieren wenn noch nicht geklickt
            // Meldung, dass Revanche nicht mehr moeglich ist, da ein Mitspieler gegangen ist.
            BackButton = Visibility.Visible;
            ReadyToPlayButton = Visibility.Hidden;
            RevancheNotPossibleWarning = Visibility.Visible;
        }

        /// <summary>
        /// Anzeige der Clients in der Gamlobby, die bereit sind zu spielen
        /// </summary>
        /// <param name="username"></param>
        private void HandlePlayerReadyToPlay(string username, bool isReady)
        {
            foreach (PlayerReadyData client in Clientlist)
            {
                if (client.Name == username)
                {
                    if (isReady)
                    {
                        //Spieler Highlighten, die bereit sind
                        client.Visibility = "Visible";
                    }
                    else
                    {
                        client.Visibility = "Hidden";
                    }
                    return;
                }
            }
        }

        /// <summary>
        /// Anzeige aller Clients, die der Gamelobby beigetreten sind
        /// </summary>
        /// <param name="userList"></param>
        private void HandleClientsInGame(string[] userList)
        {
           
            bool inList = false;
            foreach (string username in userList)
            {
                if(username != "")
                {
                    foreach (PlayerReadyData client in Clientlist)
                    {
                        if (client.Name == username)
                        {
                            inList = true;
                        }
                    }
                    if (!inList)
                    {
                        App.Current.Dispatcher.Invoke((Action)delegate
                        {
                            PlayerReadyData pd = new PlayerReadyData(username);
                            Clientlist.Add(pd);
                        });
                    
                    }
                    inList = false;
                }
            }
            
            //loeschen von Clients aus der Clientlist, die die Lobby verlassen haben
            List<PlayerReadyData> helperList = new List<PlayerReadyData>();
            foreach(PlayerReadyData client in Clientlist)
            {
                if (!userList.Contains(client.Name))
                {
                    helperList.Add(client);
                }
            }
            foreach(PlayerReadyData clientToRemove in helperList)
            {
                App.Current.Dispatcher.Invoke((Action)delegate
                {
                    Clientlist.Remove(clientToRemove);
                });
            }

        }

        /// <summary>
        /// Der Client wird vom Server benachrichtigt, dass er dem Spiel beitreten darf und welches Topic er dafuer verwenden muss.
        /// Die Methode initialisiert daraufhin einen GameProducer und GameConsumer mit dem passenden Topic.
        /// </summary>
        /// <param name="receiveFromID"></param>
        /// <param name="sendToID"></param>
        private void InitGameCommunication(string receiveFromID, string sendToID, string roomName, string fieldName)
        {
            gameConsumer.Init(BrokerManager.Instance.GetSession(), receiveFromID);
            gameProducer.Init(BrokerManager.Instance.GetSession(), sendToID, BrokerManager.Instance.GetClientDestination());
            gameProducer.ConfirmNewCommunication();
            RoomName = roomName;
            FieldName = fieldName;
        }
    }
}
