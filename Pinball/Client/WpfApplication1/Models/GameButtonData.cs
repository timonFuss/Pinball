using Client.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Client.Models
{
    class GameButtonData: ViewModelBase
    {
        private string _gameID;
        private string _roomName;
        private int _nrplayer;
        private ICommand _openGameLobbyCommand;

        public delegate void JoinGameCommandHandler(string id);
        public event JoinGameCommandHandler JoinGameEvent;

        public ICommand OpenGameLobbyCommand
        {
            get
            {
                if (_openGameLobbyCommand == null)
                {
                    _openGameLobbyCommand = new ActionCommand(dummy => JoinGameEvent(_gameID), null);
                }
                //return null?
                return _openGameLobbyCommand;
            }
        }

        public string GameID
        {
            get
            {
                return _gameID;
            }
            set
            {
                _gameID = value;
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
            }
        }

        public int NrPlayer
        {
            get
            {
                return _nrplayer;
            }
            set
            {
                _nrplayer = value;
                OnPropertyChanged("NrPlayer");
            }
        }


        public GameButtonData(string id, string name, int nr)
        {
            this._gameID = id;
            this._roomName = name;
            this._nrplayer = nr;
        }
    }
}
