using System;
using System.Windows;

namespace TestFlipper.Behaviours.Parent
{
    public class UIElementBehaviour
    {

        public double lastTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
        public double deltaTime;
        public UIElement uiElement;


        public virtual void Start() { }

        //virtuelle Update Methode die Ueberschrieben werden muss
        public virtual void Update() { }

        public virtual void Stop() { }
    }
}
