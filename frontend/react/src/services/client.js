import axios from 'axios'

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`);
    } catch (e) {
        console.log(e)
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer);
    } catch (e) {
        console.log(e)
        throw e;
    }
}

export const deleteCustomer = async (customerId) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`
        )
    } catch (e) {
        console.log(e)
        throw e;
    }
}

export const updateCustomer = async (customerId, update) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`,
            update
        );
    } catch (e) {
        console.log(e)
        throw e;
    }
}