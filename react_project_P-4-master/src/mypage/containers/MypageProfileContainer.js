import React, { useState, useCallback, useContext } from 'react';
import UserInfoContext from '../../member/modules/UserInfoContext';
import ProfileForm from '../component/ProfileForm';

const MypageProfileContainer = () => {
  const {
    states: { userInfo },
  } = useContext(UserInfoContext);
  const initialForm = userInfo;
  delete initialForm.password;

  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const onChange = useCallback((e) => {
    setForm((form) => ({ ...form, [e.target.name]: e.target.value }));
  }, []);

  const onSubmit = useCallback((e) => {
    e.preventDefault();
  }, []);

  return <ProfileForm form={form} onChange={onChange} onSubmit={onSubmit} />;
};

export default React.memo(MypageProfileContainer);
