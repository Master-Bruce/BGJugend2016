package de.schiewe.volker.bgjugend2016

import de.schiewe.volker.bgjugend2016.models.BaseEvent
import de.schiewe.volker.bgjugend2016.models.Event
import de.schiewe.volker.bgjugend2016.models.Info
import java.util.*

public val testContent: MutableList<BaseEvent> = mutableListOf(
        Event("Title1",
                Date(1, 1, 1),
                Date(1, 1, 1),
                "place1",
                "Header1",
                "Text",
                12,
                15,
                20,
                20,
                Date(1, 1, 1),
                "Team",
                "ImagePath",
                "URL"),
        Event("Title2",
                Date(1, 1, 1),
                Date(1, 1, 1),
                "place2",
                "Header2",
                "Text",
                12,
                15,
                20,
                20,
                Date(1, 1, 1),
                "Team",
                "ImagePath",
                "URL"),
        Info("TitleInfo",
                Date(1,1,1),
                Date(1,1,1),
                "Place",
                "Who",
                "registration"
        )
)