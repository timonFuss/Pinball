using System;
using Client.Models;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using TestFlipper.Behaviours.Parent;
using TestFlipper.Extensions;
using System.Windows.Media.Imaging;

namespace Client.Behaviours
{
    class VibrationChangeImageBehaviour : UIElementBehaviour
    {
        private BitmapImage originalImage;
        private BitmapImage animationImage;

        private double changeTime;

        private double changeStartTime;

        private Vector2 initialPosition;

        private bool _change;

        private int shakeAmount = 2;

        private Random rnd = new Random();

        public override void Start()
        {
            initialPosition = this.uiElement.getCanvasPosition();
            originalImage = ((Image)uiElement).Source as BitmapImage;
        }

        public void setShakyNess(int shakeValue)
        {
            shakeAmount = shakeValue;
        }

        public bool Change // wird auf true gesetzt, wenn das Objekt getroffen wurde
        {
            get
            {
                return _change;
            }
            set
            {
                _change = value;
                changeStartTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            }
        }

        public VibrationChangeImageBehaviour(double vibrationTime, int shakynessStrength, string animationImagePath)
        {
            this.changeTime = vibrationTime;
            setShakyNess(shakynessStrength);
            animationImage = new BitmapImage(new Uri(animationImagePath));
        }

        public override void Update()
        {
            double actTime = DateTime.Now.TimeOfDay.TotalMilliseconds;

            //initiale pos
            double y = initialPosition.Y;
            double x = initialPosition.X;

            // wenn vibration auf true steht und die Differenz zwischen aktueller zeit und Vibration-Start-Zeit kleiner ist als die gesamte Zeit für die Vibration
            if (_change && changeTime > actTime - changeStartTime)
            {

                this.uiElement.setCanvasPosition(x + rnd.Next(-1, 1) * shakeAmount, y + rnd.Next(-1, 1) * shakeAmount);
                ((Image)uiElement).Source = animationImage;

            }
            else
            {
                this.uiElement.setCanvasPosition(x, y);
                ((Image)uiElement).Source = originalImage;
                _change = false;
            }
        }
    }
}

