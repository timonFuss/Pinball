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
    class EditorMenuViewModel : ViewModelBase
    {
        private ICommand _backCommand;

        private MainProducer mainProducer;
        private MainConsumer mainConsumer;
        private EditorProducer editorProducer;
        private EditorConsumer editorConsumer;

        private ObservableCollection<string> _clientlist = new ObservableCollection<string>();
        private ObservableCollection<EditorButtonData> _editorlist = new ObservableCollection<EditorButtonData>();

        public ObservableCollection<EditorButtonData> Editorlist
        {
            get
            {
                return _editorlist;
            }
            set
            {
                _editorlist = value;
                OnPropertyChanged("Editorlist");
            }
        }

        public ObservableCollection<string> Clientlist
        {
            get { return _clientlist; }
            set { _clientlist = value; OnPropertyChanged("Clientlist"); }
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

                    _newRoomCommand = new ActionCommand(dummy =>OpenGamefields() , null);
                }
                return _newRoomCommand;
            }
        }

        private void OpenGamefields()
        {
            mainProducer.AskForGamefields();
            ShowGameFieldSelectionViewEvent();
        }

        public EditorMenuViewModel(MainConsumer mainConsumer, MainProducer mainProducer, EditorConsumer editorConsumer, EditorProducer editorProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;
            this.editorConsumer = editorConsumer;
            this.editorProducer = editorProducer;

            mainConsumer.ClientLoginEvent += HandleClientLoginEvent;
            mainConsumer.ClientLogoutEvent += HandleClientLogoutEvent;
            mainConsumer.ClientsOnlineEvent += HandleClientsOnlineEvent;

            mainConsumer.EditorEntryEvent += InitEditorCommunication;
            mainConsumer.OpenEditorsEvent += HandleOpenEditorsEvent;
            mainConsumer.ClosingEditorEvent += HandleClosingEditorEvent;  //ClosingGameEvent
        }

        private void HandleOpenEditorsEvent(string editorID, string roomName, int player)
        {

            // Suchen, ob Spiel in dieser Logik bereits als Button vorhanden
            foreach (EditorButtonData ebd in Editorlist)
            {
                if (ebd.EditorID.Equals(editorID))
                {
                    // Wenn gefunden Spielerzahl aktualisieren
                    ebd.Player = player;
                    return;
                }
            }


            EditorButtonData button = new EditorButtonData(editorID, roomName, player);
            button.JoinEditorEvent += HandleJoinEditorEvent;
            App.Current.Dispatcher.Invoke((Action)delegate
            {
                Editorlist.Add(button);
                OnPropertyChanged("Editorlist");
            });

        }

        /// <summary>
        /// Entfernen eines Raumbuttons
        /// </summary>
        /// <param name="gameID"></param>
        private void HandleClosingEditorEvent(string editorID)
        {
            // Suchen, ob Spiel in dieser Logik bereits als Button vorhanden
            foreach (EditorButtonData ebd in Editorlist)
            {
                if (ebd.EditorID.Equals(editorID))
                {
                    // Wenn gefunden entfernen
                    App.Current.Dispatcher.Invoke((Action)delegate
                    {
                        Editorlist.Remove(ebd);
                    });
                }
            }
            OnPropertyChanged("Editorlist");

        }

        private void HandleJoinEditorEvent(string id)
        {
            mainProducer.JoinEditor(id);
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

        public ICommand BackCommand
        {
            get
            {
                if (_backCommand == null)
                {
                    _backCommand = new ActionCommand(dummy => StepBack(), null);
                }
                //return null?
                return _backCommand;
            }
        }

        /// <summary>
        /// einen Schritt zurueck ins Hauptmenue und Clientliste anfordern
        /// </summary>
        private void StepBack()
        {
            //mainProducer.ListRequest();
            //löscht beim zurückgehen ins Menu die Liste anstonsten werden Elemente mehrfach angezeigt
            _editorlist.Clear();
            BackToMenuEvent();
        }

        private void InitEditorCommunication(string recieveFromID, string sendToID, string roomName, string fieldName)
        {
            editorConsumer.Init(BrokerManager.Instance.GetSession(), recieveFromID);
            editorProducer.Init(BrokerManager.Instance.GetSession(), sendToID, BrokerManager.Instance.GetClientDestination());
            editorProducer.ConfirmNewCommunication();
        }
    }
}
