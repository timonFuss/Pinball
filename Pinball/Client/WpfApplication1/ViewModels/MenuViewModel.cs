using Client.Communication;
using Client.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Client.ViewModels.UserControls
{
    class MenuViewModel:ViewModelBase
    {
        private ICommand _startGameCommand;
        //private ICommand _startMPGameCommand;
        private ICommand _startEditCommand;
        //private ICommand _optionsCommand;

        private MainProducer mainProducer;
        private MainConsumer mainConsumer;

        public MenuViewModel(MainConsumer mainConsumer, MainProducer mainProducer)
        {
            this.mainConsumer = mainConsumer;
            this.mainProducer = mainProducer;

        }

        public delegate void StartSPGameCommandHandler();
        public event StartSPGameCommandHandler ShowGameMenuView;

        public ICommand StartGameCommand
        {
            get
            {
                if (_startGameCommand == null)
                {

                    _startGameCommand = new ActionCommand(dummy => ShowGameMenu(), null);
                }
                //return null?
                return _startGameCommand;
            }
        }

        //Server nach allen offenen Spielen fragen
        private void ShowGameMenu()
        {
            mainProducer.AskForOpenGames();
            ShowGameMenuView();
        }


        /*        public delegate void StartMPGameCommandHandler();
                public event StartMPGameCommandHandler ShowMPGameView;

                public ICommand StartMPGameCommand
                {
                    get
                    {
                        if (_startMPGameCommand == null)
                        {

                            _startMPGameCommand = new ActionCommand(dummy => ShowMPGameView(), null);
                        }
                        //return null?
                        return _startMPGameCommand;
                    }
                }*/

        public delegate void EditGameCommandHandler();
        public event EditGameCommandHandler ShowEditorMenuView;

        public ICommand StartEditCommand
        {
            get
            {
                if (_startEditCommand == null)
                {

                    _startEditCommand = new ActionCommand(dummy => ShowEditorMenu(), null);
                }
                //return null?
                return _startEditCommand;
            }
        }

        //Server nach allen offenen Editierlobbies fragen
        private void ShowEditorMenu()
        {
            mainProducer.AskForOpenEditors();
            ShowEditorMenuView();
        }


        /*public delegate void OptionsCommandHandler();
        public event OptionsCommandHandler ShowOptionsView;

        public ICommand OptionsCommand
        {
            get
            {
                if (_optionsCommand == null)
                {

                    _optionsCommand = new ActionCommand(dummy => ShowOptionsView(), null);
                }
                //return null?
                return _optionsCommand;
            }
        }*/
    }
}
