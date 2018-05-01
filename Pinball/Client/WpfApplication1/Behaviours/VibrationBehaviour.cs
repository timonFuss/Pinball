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

namespace Client.Behaviours
{
    class VibrationBehaviour : UIElementBehaviour
    {

        private double vibrationTime;

        private double vibrationStartTime;

        private Vector2 initialPosition;

        private bool _vibrate;

        private int shakeAmount = 2;

        private Random rnd = new Random();

        public override void Start() {
            
            initialPosition = this.uiElement.getCanvasPosition();

        }

        public void setShakyNess(int shakeValue) {
            shakeAmount = shakeValue;
        }

        public bool Vibrate // wird auf true gesetzt, wenn das Objekt getroffen wurde
        {
            get
            {
                return _vibrate;
            }
            set
            {
                _vibrate = value;
                vibrationStartTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            }
        }

        public VibrationBehaviour(double vibrationTime)
        {
            this.vibrationTime = vibrationTime;
        }

        public VibrationBehaviour(double vibrationTime, int v) : this(vibrationTime)
        {
            this.vibrationTime = vibrationTime;
            setShakyNess(v);
        }

        public override void Update()
        {
            double actTime = DateTime.Now.TimeOfDay.TotalMilliseconds;

            //initiale pos
            double y = initialPosition.Y;
            double x = initialPosition.X;
            
            // wenn vibration auf true steht und die Differenz zwischen aktueller zeit und Vibration-Start-Zeit kleiner ist als die gesamte Zeit für die Vibration
            if (_vibrate && vibrationTime > actTime - vibrationStartTime)
            {
                
                this.uiElement.setCanvasPosition(x + rnd.Next(-1,1) * shakeAmount, y+ rnd.Next(-1, 1)* shakeAmount);
                
            }
            else
            {
                this.uiElement.setCanvasPosition(x, y);
                _vibrate = false;
            }
        }
    }
}
