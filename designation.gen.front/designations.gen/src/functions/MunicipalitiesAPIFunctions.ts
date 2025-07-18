import axios from "axios";

export const BASE_URL = 'http://127.0.0.1:8080'

export const GetAllMunicipalities = async () => {
    try{ 
        const response = await axios.get(`${BASE_URL}/municipalities`);
        return response.data;
    } catch (error: any){
        console.error('Erro fetching municipalities: ', error);
        throw error;
    }
}