package com.example.covid2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> equip = new ArrayList<String>();
        equip.add("Emergency First Aid Kits");
        equip.add("Hot & Cold Packs");
        equip.add(" Oxygen Tanks");
        equip.add("CPR Manikin & Masks");
        equip.add("Nebulizers");

        List<String> ambulance = new ArrayList<String>();
        ambulance.add("BD Ambulance - 0099900401");
        ambulance.add("MAISHA CARE LIMITED - 0099900401");
        ambulance.add("AD Din Medical - 0099900401");
        ambulance.add("Rent Air Ambulance- 0099900401");

        List<String> phoneno = new ArrayList<String>();
        phoneno.add("Police, fire service and ambulance services- 999");
        phoneno.add("Various government services- 333");
        phoneno.add("women and children are abused- 109");
        phoneno.add("RAB helpdesk- 101");

        expandableListDetail.put("EMERGENCY MEDICAL EQUIPMENTS", equip);
        expandableListDetail.put("AMBULANCE", ambulance);
        expandableListDetail.put("HELPLINE", phoneno);
        return expandableListDetail;
    }
}
