using Client.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.Models
{
    class PlayerReadyData: ViewModelBase
    {
        private string _name;
        private string _visibility = "Hidden";

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

        public string Visibility
        {
            get
            {
                return _visibility;
            }
            set
            {
                _visibility= value;
                OnPropertyChanged("Visibility");
            }
        }

        public PlayerReadyData(string name)
        {
            this.Name = name;
        }
    }

}
