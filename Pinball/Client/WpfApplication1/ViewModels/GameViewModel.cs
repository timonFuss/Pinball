
using Client.Helper;
using System.Windows.Input;
using Client.Communication;
using Client.Models;
using System.Collections.ObjectModel;
using System.Collections.Generic;
using System.Windows.Threading;
using System;

namespace Client.ViewModels.UserControls
{
    class GameViewModel:ViewModelBase
    {
        private GameProducer gameProducer;
        private GameConsumer gameConsumer;
        private List<PlayerInGameData> _playerlist = new List<PlayerInGameData>();
        private DispatcherTimer dispatcherTimer = new DispatcherTimer();
        private int _time = 0;
        private string _roomName = "";

        public GameModel GameModel { set; get; }

        public GameViewModel(GameProducer gameProducer, GameConsumer gameConsumer, GameModel gameModel)
        {
            this.gameProducer = gameProducer;
            this.gameConsumer = gameConsumer;

            dispatcherTimer.Tick += new EventHandler(DispatcherTimer_Tick);
            dispatcherTimer.Interval = new TimeSpan(0, 0, 1);


            gameConsumer.GameStartEvent += HandleStartTimer;
            gameConsumer.PlayerNumberListEvent += HandlePlayerNumberList;
            gameConsumer.NewScoreEvent += HandleScore;
            gameConsumer.GameOverEvent += ClearPlayerList; 
        }

        private void HandlePlayerNumberList(string username, int playerNumber)
        {
            Playerlist.Add(new PlayerInGameData(username, playerNumber));
        }

        private void HandleStartTimer(string name, int time)
        {
            GameName = name;
            Time = time;
            dispatcherTimer.Start();
        }

        private void HandleScore(string username, int score)
        {
            foreach(PlayerInGameData data in Playerlist)
            {
                if (data.Name.Equals(username))
                {
                    data.Score = score.ToString();
                }
            }
        }


        public List<PlayerInGameData> Playerlist
        {
            get
            {
                return _playerlist;
            }
            set
            {
                _playerlist = value;
                OnPropertyChanged("Playerlist");
            }
        }

        public string GameName
        {
            get
            {
                return _roomName;
            }
            set
            {
                _roomName = value;
                OnPropertyChanged("GameName");
            }
        }

        public string Timer
        {
            get
            {
                return _time + " s";
            }
        }

        public int Time
        {
            get
            {
                return _time;
            }
            set
            {
                _time = value;
                OnPropertyChanged("Timer");
            }
        }


        void DispatcherTimer_Tick(object sender, EventArgs e)
        {
            if (Time > 0)
            {
                Time--;
                if (Time == 11)
                {
                    GameModel.Instance.NotifyTenSecLeft();
                }
            }
            else
            {
                dispatcherTimer.Stop();
            }
        }

        private void ClearPlayerList(string playerName, float score)
        {
            //Parameter ignorieren
            _playerlist.Clear();
        }
    }
}
