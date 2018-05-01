using Client.Helper;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace Client.ViewModels.UserControls
{
    class AboutViewModel : ViewModelBase
    {

        public delegate void ShowLoginCommandHandler();
        public event ShowLoginCommandHandler ShowLogin;
        private ICommand _backToLoginEvent;

        public AboutViewModel()
        {
        }


        public ICommand BackToLoginEvent
        {
            get
            {
                if (_backToLoginEvent == null)
                {
                    _backToLoginEvent = new ActionCommand(dummy => ShowLogin(), null);
                }
                return _backToLoginEvent;
            }
        }
    }
}
