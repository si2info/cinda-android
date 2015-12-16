package info.si2.iista.volunteernetworks.apiclient;

import java.util.ArrayList;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 15/12/15
 * Project: Shiari
 */
public class Dictionary {

    private String name, description;
    private ArrayList<ItemDictionary> terms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ItemDictionary> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<ItemDictionary> terms) {
        this.terms = terms;
    }

}
