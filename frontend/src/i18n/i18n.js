import i18next from 'i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import { reactI18nextModule } from 'react-i18next'

import en from './en'
import ptBR from './pt-br'

i18next
    .use(LanguageDetector)
    .use(reactI18nextModule)
    .init({
        resources: {
            "en": en,
            "pt_BR": ptBR
        },
        fallbackLng: ['en', 'pt_BR'],
        debug: true,
        ns: ["translations"],
        defaultNS: "translations",
        keySeparator: false,
        interpolation: {
            formatSeparator: ","
        },
        react: {
            wait: false,
        }
    })

export default i18next
