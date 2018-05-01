using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Client.View
{
    public enum ElementType
    {
        STEIN_0,
        STEIN_1,
        STEIN_2,
        WAND_STARK_0,
        WAND_STARK_1,
        WAND_STARK_2,
        WAND_STARK_3,
        WAND_SCHWACH_0,
        WAND_SCHWACH_1,
        WAND_SCHWACH_2,
        WAND_SCHWACH_3,
        FLIPPER_PLAYER_1_LEFT,
        FLIPPER_PLAYER_1_RIGHT,
        FLIPPER_PLAYER_2_LEFT,
        FLIPPER_PLAYER_2_RIGHT,
        FLIPPER_PLAYER_3_LEFT,
        FLIPPER_PLAYER_3_RIGHT,
        FLIPPER_PLAYER_4_LEFT,
        FLIPPER_PLAYER_4_RIGHT,

        LIBERO_PLAYER_1,
        LIBERO_PLAYER_2,
        LIBERO_PLAYER_3,
        LIBERO_PLAYER_4,

        GOALPOST_PLAYER_1,
        GOALPOST_PLAYER_2,
        GOALPOST_PLAYER_3,
        GOALPOST_PLAYER_4,

        GOAL_PLAYER_1,
        GOAL_PLAYER_2,
        GOAL_PLAYER_3,
        GOAL_PLAYER_4,

        GOALAREA_PLAYER_1,
        GOALAREA_PLAYER_2,
        GOALAREA_PLAYER_3,
        GOALAREA_PLAYER_4,

        CURVE_0,
        CURVE_1,
        CURVE_2,
        CURVE_3,

        PLUNGER,
        BALL
    }

    public class GamePictures
    {
        private static GamePictures instance;
        public Dictionary<ElementType, string> pictureDict;
        /// <summary>
        /// befüllt das pictureDict mit Bildnamen für die Spielelemente, KEY sind die werte aus dem ElementType-Enum auf dem Server
        /// </summary>
        public GamePictures()
        {
            pictureDict = new Dictionary<ElementType, string>();
            pictureDict[ElementType.STEIN_0] = "Elemente_Bumper_S.png";
            pictureDict[ElementType.STEIN_1] = "Elemente_Bumper_M.png";
            pictureDict[ElementType.STEIN_2] = "Elemente_Bumper_L.png";
            pictureDict[ElementType.WAND_STARK_0] = "Element_Slingshot_0.png";
            pictureDict[ElementType.WAND_STARK_1] = "Element_Slingshot_1.png";
            pictureDict[ElementType.WAND_STARK_2] = "Element_Slingshot_2.png";
            pictureDict[ElementType.WAND_STARK_3] = "Element_Slingshot_3.png";
            pictureDict[ElementType.WAND_SCHWACH_0] = "Element_Bogen_0.png";
            pictureDict[ElementType.WAND_SCHWACH_1] = "Element_Bogen_1.png";
            pictureDict[ElementType.WAND_SCHWACH_2] = "Element_Bogen_2.png";
            pictureDict[ElementType.WAND_SCHWACH_3] = "Element_Bogen_3.png";
            pictureDict[ElementType.FLIPPER_PLAYER_1_LEFT] = "Elemente_Flipper_0_1.png";
            pictureDict[ElementType.FLIPPER_PLAYER_1_RIGHT] = "Elemente_Flipper_1_1.png";
            pictureDict[ElementType.FLIPPER_PLAYER_2_LEFT] = "Elemente_Flipper_0_2.png";
            pictureDict[ElementType.FLIPPER_PLAYER_2_RIGHT] = "Elemente_Flipper_1_2.png";
            pictureDict[ElementType.FLIPPER_PLAYER_3_LEFT] = "Elemente_Flipper_2_3.png";
            pictureDict[ElementType.FLIPPER_PLAYER_3_RIGHT] = "Elemente_Flipper_3_3.png";
            pictureDict[ElementType.FLIPPER_PLAYER_4_LEFT] = "Elemente_Flipper_2_4.png";
            pictureDict[ElementType.FLIPPER_PLAYER_4_RIGHT] = "Elemente_Flipper_3_4.png";
            pictureDict[ElementType.BALL] = "Ball.png";
            pictureDict[ElementType.LIBERO_PLAYER_1] = "Libero.png";
            pictureDict[ElementType.LIBERO_PLAYER_2] = "Libero.png";
            pictureDict[ElementType.LIBERO_PLAYER_3] = "Libero.png";
            pictureDict[ElementType.LIBERO_PLAYER_4] = "Libero.png";

            pictureDict[ElementType.GOALPOST_PLAYER_1] = "goalpost_vertical.png";
            pictureDict[ElementType.GOALPOST_PLAYER_2] = "goalpost_vertical.png";
            pictureDict[ElementType.GOALPOST_PLAYER_3] = "goalpost_horizontal.png";
            pictureDict[ElementType.GOALPOST_PLAYER_4] = "goalpost_horizontal.png";

            pictureDict[ElementType.GOAL_PLAYER_1] = "goal_player_1.png";
            pictureDict[ElementType.GOAL_PLAYER_2] = "goal_player_2.png";
            pictureDict[ElementType.GOAL_PLAYER_3] = "goal_player_3.png";
            pictureDict[ElementType.GOAL_PLAYER_4] = "goal_player_4.png";

            pictureDict[ElementType.GOALAREA_PLAYER_4] = "goalArea_player_4.png";
            pictureDict[ElementType.GOALAREA_PLAYER_2] = "goalArea_player_2.png";
            pictureDict[ElementType.GOALAREA_PLAYER_3] = "goalArea_player_3.png";
            pictureDict[ElementType.GOALAREA_PLAYER_1] = "goalArea_player_1.png";
            pictureDict[ElementType.LIBERO_PLAYER_1] = "Libero_01.png";
            pictureDict[ElementType.LIBERO_PLAYER_2] = "Libero_02.png";
            pictureDict[ElementType.LIBERO_PLAYER_3] = "Libero_03.png";
            pictureDict[ElementType.LIBERO_PLAYER_4] = "Libero_04.png";

            pictureDict[ElementType.PLUNGER] = "Plunger.png";

            pictureDict[ElementType.CURVE_0] = "Element_Curve_0.png";
            pictureDict[ElementType.CURVE_1] = "Element_Curve_1.png";
            pictureDict[ElementType.CURVE_2] = "Element_Curve_2.png";
            pictureDict[ElementType.CURVE_3] = "Element_Curve_3.png";
        }

        // Instanz-Property für das Singleton
        public static GamePictures Instance
        {
            get
            {
                if (instance == null)
                    instance = new GamePictures();
                return instance;
            }
        }
    }
}
