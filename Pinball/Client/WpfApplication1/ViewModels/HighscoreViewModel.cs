using Client.Helper;
using System.Collections.Generic;
using Client.Communication;
using Client.Models;
using System.Windows.Input;
using System;

namespace Client.ViewModels.UserControls
{
    class HighscoreViewModel : ViewModelBase
    {
        private GameConsumer gameConsumer;
        private GameProducer gameProducer;
        private List<PlayerInGameData> _highscore = new List<PlayerInGameData>();
        private string _revanchePossible = "True";

        public delegate void BackToMenuCommandHandler();
        public event BackToMenuCommandHandler BackToMenu;
        private ICommand _backToMenuCommand;
        public ICommand BackToMenuCommand
        {
            get
            {
                if (_backToMenuCommand == null)
                {

                    _backToMenuCommand = new ActionCommand(dummy => LeaveGame(), null);
                }
                return _backToMenuCommand;
            }
        }
        private void LeaveGame()
        {
            // wenn man aus der Highscore-View wegnavigiert, resette das GameModel
            GameModel.Instance.ClearGameModel();
            // und die Highscores
            Highscore.Clear();

            BackToMenu();

            // Beim Server vom Spielraum abmelden
            // und Kommunikationswege beenden
            gameConsumer.Terminate();
            gameProducer.LeaveCommunication();
            gameProducer.Terminate();
        }

        public delegate void BackToGameLobbyCommandHandler();
        public event BackToGameLobbyCommandHandler BackToGameLobby;
        private ICommand _backToGameLobbyCommand;
        public ICommand BackToGameLobbyCommand
        {
            get
            {
                if (_backToGameLobbyCommand == null)
                {
                    _backToGameLobbyCommand = new ActionCommand(dummy => GameLobbyAgain(), null);
                }
                //return null?
                return _backToGameLobbyCommand;
            }
        }
        private void GameLobbyAgain()
        {
            // wenn man aus der Highscore-View wegnavigiert, resette das GameModel
            GameModel.Instance.ClearGameModel();
            // und die Highscores
            Highscore.Clear();
            RevanchePossible = "True";

            BackToGameLobby();
        }


        public List<PlayerInGameData> Highscore
        {
            get
            {
                return _highscore;
            }
            set
            {
                _highscore = value;
                OnPropertyChanged("Highscore");
            }
        }

        public string RevanchePossible
        {
            get
            {
                return _revanchePossible;
            }
            set
            {
                _revanchePossible = value;
                OnPropertyChanged("RevanchePossible");
            }
        }

        public HighscoreViewModel(GameConsumer gameConsumer, GameProducer gameProducer)
        {
            this.gameConsumer = gameConsumer;
            this.gameProducer = gameProducer;
            gameConsumer.GameOverEvent += HandleShowHighscoreView;
            gameConsumer.NoRevancheEvent += HandleNoRevancheEvent;
        }



        private void HandleShowHighscoreView(string playerName, float score_int)
        {
            Highscore.Add(new PlayerInGameData(playerName, score_int.ToString()));
        }

        /// <summary>
        /// Revanche nicht mehr umsetzbar. Revanche-Button nicht mehr klickbar.
        /// </summary>
        private void HandleNoRevancheEvent()
        {
            RevanchePossible = "False";
        }

        /// <summary>
        /// View zurücksetzen
        /// </summary>
        public void reset()
        {
            RevanchePossible = "True";
        }
    }
}
