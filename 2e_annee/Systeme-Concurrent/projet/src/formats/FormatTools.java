package formats;

import exceptions.FormatNotReferenced;

/*Ensemble de fonction utilitaire sur les Formats*/
public class FormatTools {
    /* Crée et renvoi une instance du format associée au Format.Type fourni*/
    public static Format SelectFormat(Format.Type fmt){
    	// ===================================================================================================
        // INSÉRER ICI TOUTES LES CLASSES DE Format POSSIBLE À LA MANIÈRE DE MonExempleFormat CI-DESSOUS
        // case MonExempleFormat:
        //     return new MonExempleFormat();
        // AJOUTER CE NOUVEAU TYPE À L'ÉNUMERATION Format.Type DE LA CLASSE Format
        //   MonExempleFormat,
        // ET PENSER À IMPORTER LA CLASSE EN DÉBUT DE FICHIER.
        // import application.MonExempleFormat;
        // ===================================================================================================
        Format lfreader = null;
        switch (fmt) {
            case LINE:
                lfreader = new LineFormat();
                break;
            case KV:
                lfreader = new KVFormat();
                break;
            default:
            	throw new FormatNotReferenced();
        }
        return lfreader;
    }
}
