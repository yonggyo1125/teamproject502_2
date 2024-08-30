import React, { useState, useCallback, useContext } from 'react';
import UserInfoContext from '../../member/modules/UserInfoContext';

const MypageProfileContainer = () => {
  const {
    states: { userInfo },
  } = useContext(UserInfoContext);
  const initialForm = userInfo;
  delete initialForm.password;

  const [form, setForm] = useState(initialForm);

  const onChange = useCallback((e) => {
    setForm((form) => ({ ...form, [e.target.name]: e.target.value }));
  }, []);

  return <></>;
};

export default React.memo(MypageProfileContainer);
