import axios from "axios";
import type { Designation } from "../data/DesignationsData";

export const BaseURL = 'http://127.0.0.1:8080/api';

let cachedDesignations: Designation | null = null;
let cachedGeneratedDesignation: Designation | null = null;

export const clearDesignationsCache = () => { cachedDesignations = null; };
export const clearSelectedDesignationCache = () => { cachedGeneratedDesignation = null; };
export const clearAllDesignationsCache = () => {
  cachedDesignations = null;
  cachedGeneratedDesignation = null;
};

export const SetCachedGeneratedDesignation = (designation: Designation | null) => {
  cachedGeneratedDesignation = designation;
  if (designation) {
    console.log('Designation cached:', designation);
  } else {
    console.log('Designation cache cleared');
  }
}

export const GetCachedGeneratedDesignation = async () => {
    try {
        const response = await axios.post(`${BaseURL}/designations-generator`, cachedDesignations);
        const DesignationResponse = response.data as JSON;
        return DesignationResponse;
    } catch (error: any) {
        console.error('Failed to generate designation:', error);
        throw error;
    }
};