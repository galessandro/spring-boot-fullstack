import { Wrap, WrapItem, Spinner, Text } from "@chakra-ui/react";
import SidebarWithHeader from "./components/shared/SideBar";
import { useEffect, useState } from "react";
import { getCustomers } from "./services/client";
import CardWithImage from "./components/Card";
import DrawerForm from "./components/CreateCustomerDrawer";
import { errorNotification } from "./services/notification";

const App = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [err, setError] = useState("");

  const fetchCustomers = () => {
    setLoading(true);
    getCustomers()
      .then((res) => {
        setCustomers(res.data);
        console.log(res);
      })
      .catch((err) => {
        console.log(err);
        setError(err.response.data.message)
        
        errorNotification(
          err.code, 
          err.response.data.message
        );
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  if (loading) {
    return (
      <SidebarWithHeader>
        <Spinner
          thickness="4px"
          speed="0.65s"
          emptyColor="gray.200"
          color="blue.500"
          size="xl"
        />
      </SidebarWithHeader>
    );
  }

  if(err){
    return (
      <SidebarWithHeader>
        <DrawerForm fetchCustomers={fetchCustomers} />
        <Text mt={5}>Ooops there was an error</Text>
      </SidebarWithHeader>
    );
  }

  if (customers.length <= 0) {
    return (
      <SidebarWithHeader>
        <DrawerForm fetchCustomers={fetchCustomers} />
        <Text mt={5}>No customers available</Text>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <DrawerForm fetchCustomers={fetchCustomers} />
      <Wrap justify={"center"} spacing={"30px"}>
        {customers.map((customer, index) => (
          <WrapItem key={index}>
            <CardWithImage {...customer} imageNumber={index} fetchCustomers={fetchCustomers} />
          </WrapItem>
        ))}
      </Wrap>
    </SidebarWithHeader>
  );
};

export default App;
