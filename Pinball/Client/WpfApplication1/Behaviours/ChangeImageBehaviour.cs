using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Media.Imaging;
using TestFlipper.Behaviours.Parent;

namespace Client.Behaviours
{
    class ChangeImageBehaviour : UIElementBehaviour
    {
        private BitmapImage originalImage;

        private BitmapImage animationImage;
        private double animationTime;

        private double animationStartTime;

        private bool _objectHit;
        public bool ObjectHit // wird auf true gesetzt, wenn das Objekt getroffen wurde
        {
            get
            {
                return _objectHit;
            }
            set
            {
                _objectHit = value;
                animationStartTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            }
        }


        public ChangeImageBehaviour(string animationImagePath, double animationTime)
        {
            animationImage = new BitmapImage(new Uri(animationImagePath));
            this.animationTime = animationTime;
        }


        public override void Start()
        {
            //startUpdate
            originalImage = ((Image)uiElement).Source as BitmapImage;
        }

        public override void Update()
        {
            double actTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            // wenn objectHit auf true steht und die Differenz zwischen aktueller zeit und Animations-Start-Zeit kleiner ist als die gesamte Zeit für die Animation
            // zeige das animations-Bild an
            if (_objectHit && animationTime > actTime - animationStartTime)
            {
                ((Image)uiElement).Source = animationImage;
            }
            else
            {
                _objectHit = false;
                ((Image)uiElement).Source = originalImage;
            }
           
        }

        public override void Stop()
        {
            //stopUpdate
        }
    }
}
