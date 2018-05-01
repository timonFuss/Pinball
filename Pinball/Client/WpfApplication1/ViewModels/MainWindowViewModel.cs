using System;
using Client.ViewModels.UserControls;
using Client.Helper;
using Client.Communication;
using System.ComponentModel;
using Client.Models;
using System.Windows;

namespace Client.ViewModels.UserControls
{
    class MainWindowViewModel : ViewModelBase
    {
        private MainProducer mainProducer;
        private MainConsumer mainConsumer;
        private GameProducer gameProducer;
        private GameConsumer gameConsumer;
        private EditorProducer editorProducer;
        private EditorConsumer editorConsumer;

        private LoginViewModel loginViewModel;
        private MenuViewModel menuViewModel;
        private GameMenuViewModel gameMenuViewModel;
        private EditorMenuViewModel editorMenuViewModel;
        private GameViewModel gameViewModel;
        private HighscoreViewModel highscoreViewModel;
        private EditorViewModel editorViewModel;
        private GameFieldSelectionViewModel gameFieldSelectionViewModel;
        private AboutViewModel aboutViewModel;




        private GameLobbyViewModel gameLobbyViewModel;

        private ViewModelBase _currentUserControl;

        public MainWindowViewModel()
        {
            mainProducer = new MainProducer();
            mainConsumer = new MainConsumer();
            gameProducer = new GameProducer();
            gameConsumer = new GameConsumer();
            editorProducer = new EditorProducer();
            editorConsumer = new EditorConsumer();

            GameModel.Instance.GameProducer = gameProducer;
            GameModel.Instance.GameConsumer = gameConsumer;
            EditorModel.Instance.EditorProducer = editorProducer;
            
            loginViewModel = new LoginViewModel(mainConsumer, mainProducer);
            menuViewModel = new MenuViewModel(mainConsumer, mainProducer);
            gameMenuViewModel = new GameMenuViewModel(mainConsumer, mainProducer);
            editorMenuViewModel = new EditorMenuViewModel(mainConsumer, mainProducer,editorConsumer,editorProducer);
            gameViewModel = new GameViewModel(gameProducer, gameConsumer, GameModel.Instance);
            editorViewModel = new EditorViewModel(editorProducer,editorConsumer, EditorModel.Instance);
            gameLobbyViewModel = new GameLobbyViewModel(mainConsumer, mainProducer, gameConsumer, gameProducer);
            highscoreViewModel = new HighscoreViewModel(gameConsumer, gameProducer);
            gameFieldSelectionViewModel = new GameFieldSelectionViewModel(mainConsumer, mainProducer);
            aboutViewModel = new AboutViewModel();

            
            //Consumer-Events
            mainConsumer.LoginOkEvent += HandleLoginOk;

            gameConsumer.SetPosXEvent += GameModel.Instance.SetPosX;
            gameConsumer.SetPosYEvent += GameModel.Instance.SetPosY;
            gameConsumer.SetTypeEvent += GameModel.Instance.SetType;
            gameConsumer.SetChangeableEvent += GameModel.Instance.SetChangeableInfo;
            gameConsumer.GameLoadMessageProcessCompleteEvent += HandleShowGameView;
            
            gameConsumer.UpdatePosXEvent += GameModel.Instance.UpdatePosX;
            gameConsumer.UpdatePosYEvent += GameModel.Instance.UpdatePosY;
            gameConsumer.UpdateRotationEvent += GameModel.Instance.UpdateRotation;

            editorConsumer.SetTypeEvent += EditorModel.Instance.SetType;
            editorConsumer.SetPosXEvent += EditorModel.Instance.SetPosX;
            editorConsumer.SetPosYEvent += EditorModel.Instance.SetPosY;
            editorConsumer.ElementSetEvent += EditorModel.Instance.SetElement;
            editorConsumer.ElementDeletedEvent += EditorModel.Instance.DeleteElement;
            editorConsumer.ElementMovedEvent += EditorModel.Instance.MoveElement;
            editorConsumer.EditorLoadMessageProcessCompleteEvent += HandleShowEditorView;
            editorConsumer.EditorLoadMessageProcessCompleteEvent += EditorModel.Instance.SetLoadingCompleteEvent; ;

            //Start bei LoginView
            CurrentUserControl = loginViewModel;

            //aus Hauptmenü
            menuViewModel.ShowGameMenuView += HandleShowGameMenuView;
            menuViewModel.ShowEditorMenuView += HandleShowEditorMenuView;

            //Zurück zum Menü
            editorMenuViewModel.BackToMenuEvent += HandleShowMenuView;
            gameMenuViewModel.BackToMenuEvent += HandleShowMenuView;
            editorViewModel.BackToMenuEvent += HandleShowEditorMenuView;

            //einen Schritt zurück
            gameLobbyViewModel.StepBackEvent += HandleShowGameMenuView;

            //von GameMenuView zu GamelobbyView wechseln
            mainConsumer.GameLobbyEntryEvent += HandleShowGameLobbyView;
            mainConsumer.GameLobbyEntryDeniedEvent += HandleShowGameLobbyEntryDenied;

            //von EditorMenuView zu EditorView wechseln
            mainConsumer.EditorEntryEvent += HandleSetEditorGUIVariables;

            //GameFieldSelectionView anzeigen
            gameMenuViewModel.ShowGameFieldSelectionViewEvent += HandleShowGameFieldSelectionView_Game;     //von GameMenu aus
            editorMenuViewModel.ShowGameFieldSelectionViewEvent += HandleShowGameFieldSelectionView_Editor; // von EditorMenu aus

            //aus GameFieldSelectionView zurueck in GameMenuView
            gameFieldSelectionViewModel.ShowGameMenuEvent += HandleShowGameMenuView;
            gameFieldSelectionViewModel.ShowEditorMenuEvent += HandleShowEditorMenuView;

            //Spiel ist beendet und Highscore soll angezeigt werden
            gameConsumer.GameOverEvent += HandleShowHighscoreView;

            //Navigation aus Highscoreliste zum Hauptbildschirm oder zur GameLobbyView
            highscoreViewModel.BackToMenu += HandleShowMenuView;
            highscoreViewModel.BackToGameLobby += HandleShowGameLobbyViewAgain;

            //Credits
            loginViewModel.ShowCredits += HandleShowCreditsView;
            aboutViewModel.ShowLogin += HandleShowLoginView;

            //bei Verbindungsabbruch
            mainProducer.ConnectionLostEvent += HandleConnectionLost;
            gameProducer.ConnectionLostEvent += HandleConnectionLost;
            editorProducer.ConnectionLostEvent += HandleConnectionLost;
        }

        private void HandleShowGameFieldSelectionView_Game()
        {
            gameFieldSelectionViewModel.reset();
            gameFieldSelectionViewModel.GameMode = true;
            CurrentUserControl = gameFieldSelectionViewModel;
        }

        private void HandleShowGameFieldSelectionView_Editor()
        {
            gameFieldSelectionViewModel.reset();
            gameFieldSelectionViewModel.GameMode = false;
            CurrentUserControl = gameFieldSelectionViewModel;
        }

        private void HandleShowEditorView()
        {
            editorViewModel.reset();
            CurrentUserControl = editorViewModel;
        }

        private void HandleShowHighscoreView(string playerName, float score)
        {
            highscoreViewModel.reset();
            CurrentUserControl = highscoreViewModel;
        }

        private void HandleShowGameLobbyEntryDenied()
        {
            Console.WriteLine("Zu viele Spieler!");
        }

        private void HandleShowGameLobbyView(string receiveFromID, string sendToID, string roomName, string fieldName)
        {
            gameLobbyViewModel.reset();
            CurrentUserControl = gameLobbyViewModel;
   
        }

        private void HandleShowGameLobbyViewAgain()
        {
            gameLobbyViewModel.reset();
            CurrentUserControl = gameLobbyViewModel;

        }

        /// <summary>
        /// Falls der Login erfolgreich war, wird das Hauptmenue angezeigt und vom Server eine Liste aller Clients angefordert, die online sind.
        /// </summary>
        /// <param name="isOk"></param>
        private void HandleLoginOk(bool isOk)
        {
            if (isOk)
            {
                CurrentUserControl = menuViewModel;
                mainProducer.ListRequest();
            }

        }

        private void HandleShowGameView()
        {
            CurrentUserControl = gameViewModel;
            gameLobbyViewModel.ResetPlayerLobbyList();
        }

        private void HandleShowMenuView()
        {
            editorMenuViewModel.Editorlist.Clear();
            editorViewModel.Editorlist.Clear();
            gameMenuViewModel.Gamelist.Clear();
            CurrentUserControl = menuViewModel;
        }

        private void HandleShowEditorMenuView()
        {
            CurrentUserControl = editorMenuViewModel;
        }
        
        private void HandleSetEditorGUIVariables(string receiveFromID, string sendToID, string roomName, string fieldName)
        {
            editorViewModel.RoomName = roomName;
            editorViewModel.TemplateName = fieldName;
        }

        private void HandleShowGameMenuView()
        {
            gameLobbyViewModel.ClearPlayerLobbyList();
            CurrentUserControl = gameMenuViewModel;
        }

        private void HandleLoginIsOk()
        {
            CurrentUserControl = menuViewModel;
        }

        private void HandleShowCreditsView()
        {
            CurrentUserControl = aboutViewModel;
        }

        private void HandleShowLoginView()
        {
            CurrentUserControl = loginViewModel;
        }

        public ViewModelBase CurrentUserControl
        {
            get
            {
                return _currentUserControl;
            }
            set
            {
                _currentUserControl = value;
                OnPropertyChanged("CurrentUserControl");
            }
        }

        /// <summary>
        // wird angetoßen wenn der Client den Server nicht mehr erreicht
        /// </summary>
        private void HandleConnectionLost()
        {
            // wechsle zur Login-View und zeige eine Fehlermeldung an
            loginViewModel.ErrorText = "Verbindung zum Server unterbrochen!";
            loginViewModel.ErrorVisibility = Visibility.Visible;
            CurrentUserControl = loginViewModel;
            BrokerManager.Instance.ConnectionLost(); // sag dem Broker, dass er wieder initialisierbar ist
        }

        /// <summary>
        /// Logout durch schließen des Fensters
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        internal void OnClosing(object sender, CancelEventArgs e)
        {
            mainProducer.Logout();
            BrokerManager.Instance.CloseConnection();
        }
    }
}
