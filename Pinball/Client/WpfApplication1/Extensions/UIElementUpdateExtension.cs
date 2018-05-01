using System;
using System.Collections.Generic;
using System.Numerics;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using TestFlipper.Behaviours.Parent;

namespace TestFlipper.Extensions
{
    //Ein paar Dinge die die ganzen Verrueckten sachen moeglich machen

    //extension von UIElement objekt um eine Update methode und ein dic das UI Objekte und deren zugewiesenen update methoden beinhaltet
    public static class UIElementUpdateExtension
    {
        //todo mache aus der function eine liste von functions und dynamischadden und removen soll moeglich sein
        //delta time rein
        public static Dictionary<UIElement, UIElementBehaviour> updateMethodDic = new Dictionary<UIElement, UIElementBehaviour>();

        public static void setCanvasPosition(this UIElement uie, Object posX, Object posY)
        {
            uie.SetValue(Canvas.TopProperty, posY);
            uie.SetValue(Canvas.LeftProperty, posX);
        }

        public static Vector2 getCanvasPosition(this UIElement uie)
        {
            return new Vector2((float)((double)uie.GetValue(Canvas.LeftProperty)),(float)((double)uie.GetValue(Canvas.TopProperty)));
        }

        public static void Update(this UIElement uie, object o, EventArgs args)
        {
            if (updateMethodDic.ContainsKey(uie))
            {
                //callt die update methode
                updateMethodDic[uie].Update();

                //nimmt Delta time und merkt sie sich
                updateMethodDic[uie].deltaTime = DateTime.Now.TimeOfDay.TotalMilliseconds - updateMethodDic[uie].lastTime;

                //restartet Delta time
                updateMethodDic[uie].lastTime = DateTime.Now.TimeOfDay.TotalMilliseconds;
            }

        }

        public static void addUIElementBehaviour(this UIElement uie, UIElementBehaviour f)
        {
            //CompositionTarget.Rendering += uie.Update;
            f.uiElement = uie;

            updateMethodDic.Add(uie, f);
            //elapsedMethodTimeDic.Add(uie, f.deltaTimeWatch);
        }

        public static UIElementBehaviour getUIElementBehaviour(this UIElement uie)
        {
            if (updateMethodDic.ContainsKey(uie))
            {
                return updateMethodDic[uie];
            }
            return null;
        }
        public static void removeUIElementBehaviour(this UIElement uie)
        {
            CompositionTarget.Rendering -= uie.Update;
            if (updateMethodDic.ContainsKey(uie))
            {
                updateMethodDic.Remove(uie);
            }
            
        }
        public static void stopUpdate(this UIElement uie)
        {
            
            if (updateMethodDic.ContainsKey(uie))
            {
                updateMethodDic[uie].Start();
            }
            
        }
        public static void startUpdate(this UIElement uie)
        {
            CompositionTarget.Rendering += uie.Update;
            if (updateMethodDic.ContainsKey(uie)) {
                updateMethodDic[uie].Start();
            }
            
        }
    }
}
