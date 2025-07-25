import axios from 'axios';
import type { Municipalities } from '../data/MunicipalitiesData';


export const BaseURL = 'http://127.0.0.1:8080';

let cachedDataMunicipalities: Municipalities[] | null = null;
let cachedSelectedMunicipality: Municipalities | null = null;

export const clearMunicipalitiesCache = () => { cachedDataMunicipalities = null; };
export const clearSelectedMunicipalityCache = () => { cachedSelectedMunicipality = null; };
export const clearAllCache = () => {
  cachedDataMunicipalities = null;
  cachedSelectedMunicipality = null;
};


export const SetCachedSelectedMUnicipalities = (municipality: Municipalities | null) => {
  cachedSelectedMunicipality = municipality;
  if (municipality) {
    console.log('Municipality cached:', municipality);
  } else {
    console.log('Municipality cache cleared');
  }
}
export const GetCachedSelectedMunicipalities = () => cachedSelectedMunicipality;


export const GetAllMunicipalities = async () => {
  try {
    const response = await axios.get(`${BaseURL}/municipalities`);
    return response.data;
  } catch (error: any) {
    console.error('Failed to fetch municipalities:', error);
    throw error;
  }
}