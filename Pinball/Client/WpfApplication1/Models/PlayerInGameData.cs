using Client.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Models
{
    class PlayerInGameData: ViewModelBase
    {
        private string _name;
        private int _playerNr;
        private string _score = "100";


        public int PlayerNr
        {
            get
            {
                return _playerNr;
            }
            set
            {
                _playerNr = value;
            }
        }



        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                _name = value;
                OnPropertyChanged("Name");
            }
        }

        public string PlayerIcon
        {
            get
            {
                return "../../Resources/Pictures/playercolor_" + _playerNr + ".png";
            }
        }

        public string Score
        {
            get
            {
                return _score;
            }
            set
            {
                _score = value;
                OnPropertyChanged("Score");
            }
        }



        public PlayerInGameData(string name, int nr)
        {
            Console.WriteLine("\r\nName: " + name + " / Nummer: " + nr + "\r\n");
            this.Name = name;
            this.PlayerNr = nr;
        }

        public PlayerInGameData(string name, string score)
        {
            this.Name = name;
            this.Score = score;
        }
    }
}
