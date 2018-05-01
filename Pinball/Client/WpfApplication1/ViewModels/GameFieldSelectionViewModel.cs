using Client.Communication;
using Client.Helper;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows.Input;
using System;

namespace Client.ViewModels.UserControls
{
    class GameFieldSelectionViewModel : ViewModelBase
    {
        private MainConsumer mainConsumer;
        private MainProducer mainProducer;
        private bool _gameMode = false;
        private List<string> _gamefields = new List<string>();
        private string _selectedGamefield = "";
        private string _roomName = "";
        private bool _sek_30 = false;
        private bool _sek_60 = false;
        private bool _sek_120 = false;
        private string _visibility = "Visible";
        private int time = 0;
        private string _buttonText = "OK";


        public bool GameMode
        {
            get
            {
                return _gameMode;
            }
            set
            {
                _gameMode = value;
                if(!GameMode)
                {
                    Visibility = "Hidden";
                    ButtonText = "Editierraum öffnen";
                }else
                {
                    Visibility = "Visible";
                    ButtonText = "Spielraum öffnen";
                }
            }
        }


        public List<string> Gamefields
        {
            get
            {
                return _gamefields;
            }
            set
            {
                _gamefields = value;
                OnPropertyChanged("Gamefields");
            }
        }

        public string SelectedGamefield
        {
            get
            {
                return _selectedGamefield;
            }
            set
            {
                _selectedGamefield = value;
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


        public string ButtonText
        {
            get
            {
                return _buttonText;
            }
            set
            {
                _buttonText = value;
                OnPropertyChanged("ButtonText");
            }
        }

        public bool Sek_30
        {
            get
            {
                return _sek_30;
            }
            set
            {
                
                _sek_30 = value;
                if (_sek_30) time = 30;
                Console.WriteLine("sek_30: " + _sek_30 + ", time: " + time);
            }
        }

        public bool Sek_60
        {
            get
            {
                return _sek_60;
            }
            set
            {
                _sek_60 = value;
                if (_sek_60) time = 60;
                Console.WriteLine("sek_60: " + _sek_60 + ", time: " + time);
            }
        }
        public bool Sek_120
        {
            get
            {
                return _sek_120;
            }
            set
            {
                _sek_120 = value;
                if (_sek_120) time = 120;
                Console.WriteLine("sek_120: " + _sek_120 + ", time: " + time);
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
                _visibility = value;
                OnPropertyChanged("Visibility");
            }
        }

        private ICommand _chooseGamefieldCommand;
        public ICommand ChooseGamefieldCommand
        {
            get
            {
                if (GameMode)
                {
                    _chooseGamefieldCommand = new ActionCommand(dummy => mainProducer.StartGame(SelectedGamefield, RoomName, time), dummy => RoomName != "" && time != 0 && SelectedGamefield != "");
                }
                else
                {
                    _chooseGamefieldCommand = new ActionCommand(dummy => mainProducer.StartEditor(SelectedGamefield, RoomName), dummy => RoomName != "" && SelectedGamefield != "");
                }
                return _chooseGamefieldCommand;
            }
        }


        public delegate void StepBackCommandHandler();
        public event StepBackCommandHandler ShowGameMenuEvent;
        public event StepBackCommandHandler ShowEditorMenuEvent;

        private ICommand _stepbackCommand;

        public ICommand StepBackCommand
        {
            get
            {
                if (_stepbackCommand == null)
                {

                    _stepbackCommand = new ActionCommand(dummy => StepBack(), null);
                }
                return _stepbackCommand;
            }
        }

        private void StepBack()
        {
            if (GameMode)
            {
                ShowGameMenuEvent();
            }
            else
            {
                ShowEditorMenuEvent();
            }
        }

        public GameFieldSelectionViewModel(MainConsumer mainConsumer, MainProducer mainProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;

            mainConsumer.AvailableFieldsEvent += HandleAvailableFieldsEvent;
        }

        public void reset()
        {
            RoomName = "";
        }

        private void HandleAvailableFieldsEvent(string[] fieldList)
        {
            Gamefields.Clear();

            foreach (string fieldname in fieldList)
            {
                if (fieldname != "")
                {
                    Console.WriteLine(fieldname);
                    Gamefields.Add(fieldname);
                }
            }
        }
    }
}
